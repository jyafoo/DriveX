package com.atguigu.daijia.dispatch.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.dispatch.mapper.OrderJobMapper;
import com.atguigu.daijia.dispatch.service.NewOrderService;
import com.atguigu.daijia.dispatch.xxl.client.XxlJobClient;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.entity.dispatch.OrderJob;
import com.atguigu.daijia.model.enums.OrderStatus;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import com.atguigu.daijia.model.vo.order.NewOrderDataVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class NewOrderServiceImpl implements NewOrderService {

    private final XxlJobClient xxlJobClient;
    private final OrderJobMapper orderJobMapper;
    private final LocationFeignClient locationFeignClient;
    private final OrderInfoFeignClient orderInfoFeignClient;
    private final RedisTemplate redisTemplate;
    private final ReactiveRedisTemplate reactiveRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addAndStartTask(NewOrderTaskVo newOrderTaskVo) {
        // TODO (JIA,2024/8/21,19:35) 亮点三：通过xxl-job实现乘客下单搜索附近代驾司机（添加并开始新订单任务调度）
        // 1、判断当前订单是否启动任务调度
        // 根据订单id查询
        OrderJob orderJob = orderJobMapper.selectOne(new LambdaQueryWrapper<OrderJob>().eq(OrderJob::getOrderId, newOrderTaskVo.getOrderId()));

        // 2 没有启动，进行操作
        if (orderJob == null) {
            // 创建并启动任务调度
            // String executorHandler 执行任务job方法
            // String param
            // String corn 执行cron表达式
            // String desc 描述信息
            Long jobId = xxlJobClient.addAndStart(
                    "newOrderTaskHandler",
                    "",
                    "0 0/1 * * * ?",
                    "新订单任务,订单id：" + newOrderTaskVo.getOrderId()
            ); // 每1分钟执行一次，处理任务的bean为：newOrderTaskHandler

            // 记录订单与任务的关联信息
            orderJob = new OrderJob();
            orderJob.setOrderId(newOrderTaskVo.getOrderId());
            orderJob.setJobId(jobId);
            orderJob.setParameter(JSONObject.toJSONString(newOrderTaskVo));
            orderJobMapper.insert(orderJob);
        }
        return orderJob.getJobId();
    }


    @Override
    public void executeTask(long jobId) {
        // TODO (JIA,2024/8/21,20:06) 亮点三：通过xxl-job实现乘客下单搜索附近代驾司机（执行任务：搜索附近司机）
        // 1 根据jobid查询数据库，当前任务是否已经创建
        // 如果没有创建，不往下执行了
        OrderJob orderJob = orderJobMapper.selectOne(new LambdaQueryWrapper<OrderJob>().eq(OrderJob::getJobId, jobId));
        if (null == orderJob) {
            return;
        }
        NewOrderTaskVo newOrderTaskVo = JSONObject.parseObject(orderJob.getParameter(), NewOrderTaskVo.class);

        // 2 查询订单状态，如果当前订单接单状态，继续执行。如果当前订单不是接单状态，停止任务调度
        // 获取OrderJob里面对象
        Long orderId = newOrderTaskVo.getOrderId();
        Integer orderStatus = orderInfoFeignClient.getOrderStatus(orderId).getData();
        if (orderStatus.intValue() != OrderStatus.WAITING_ACCEPT.getStatus().intValue()) {
            xxlJobClient.stopJob(jobId);
            log.info("停止任务调度: {}", JSON.toJSONString(newOrderTaskVo));
            return;
        }

        // 3 远程调用:搜索附近满足条件可以接单司机
        SearchNearByDriverForm searchNearByDriverForm = new SearchNearByDriverForm();
        searchNearByDriverForm.setLongitude(newOrderTaskVo.getStartPointLongitude());
        searchNearByDriverForm.setLatitude(newOrderTaskVo.getStartPointLatitude());
        searchNearByDriverForm.setMileageDistance(newOrderTaskVo.getExpectDistance());
        List<NearByDriverVo> nearByDriverVoList = locationFeignClient.searchNearByDriver(searchNearByDriverForm).getData();

        // 4 遍历司机集合，得到每个司机，为每个司机创建临时队列，存储新订单信息
        nearByDriverVoList.forEach(driver -> {
            // 记录司机id，防止重复推送订单信息
            String repeatKey = RedisConstant.DRIVER_ORDER_REPEAT_LIST + newOrderTaskVo.getOrderId();
            boolean isMember = redisTemplate.opsForSet().isMember(repeatKey, driver.getDriverId());
            if (!isMember) {
                // 记录该订单已放入司机容器
                redisTemplate.opsForSet().add(repeatKey,driver.getDriverId());
                // 过期时间：15分钟，新订单15分钟没人接单自动取消
                redisTemplate.expire(
                        repeatKey,
                        RedisConstant.DRIVER_ORDER_REPEAT_LIST_EXPIRES_TIME,
                        TimeUnit.MINUTES
                );

                // 封装新订单数据返回对象
                NewOrderDataVo newOrderDataVo = new NewOrderDataVo();
                newOrderDataVo.setOrderId(newOrderTaskVo.getOrderId());
                newOrderDataVo.setStartLocation(newOrderTaskVo.getStartLocation());
                newOrderDataVo.setEndLocation(newOrderTaskVo.getEndLocation());
                newOrderDataVo.setExpectAmount(newOrderTaskVo.getExpectAmount());
                newOrderDataVo.setExpectDistance(newOrderTaskVo.getExpectDistance());
                newOrderDataVo.setExpectTime(newOrderTaskVo.getExpectTime());
                newOrderDataVo.setFavourFee(newOrderTaskVo.getFavourFee());
                newOrderDataVo.setDistance(driver.getDistance());
                newOrderDataVo.setCreateTime(newOrderTaskVo.getCreateTime());

                // 将消息保存到司机的临时队列里面，司机接单了会定时轮询到他的临时队列获取订单消息
                String key = RedisConstant.DRIVER_ORDER_TEMP_LIST+driver.getDriverId();

                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(newOrderDataVo)); // 添加到指定的List的最左侧
                //过期时间：1分钟，1分钟未消费，自动过期
                //注：司机端开启接单，前端每5秒（远小于1分钟）拉取1次“司机临时队列”里面的新订单消息
                redisTemplate.expire(
                        key,
                        RedisConstant.DRIVER_ORDER_TEMP_LIST_EXPIRES_TIME,
                        TimeUnit.MINUTES
                );
                log.info("该新订单信息已放入司机临时队列: {}", JSON.toJSONString(newOrderDataVo));
            }
        });
    }


}
