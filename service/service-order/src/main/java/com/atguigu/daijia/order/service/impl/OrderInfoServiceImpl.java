package com.atguigu.daijia.order.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.model.entity.order.*;
import com.atguigu.daijia.model.enums.OrderStatus;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.form.order.StartDriveForm;
import com.atguigu.daijia.model.form.order.UpdateOrderBillForm;
import com.atguigu.daijia.model.form.order.UpdateOrderCartForm;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.order.*;
import com.atguigu.daijia.order.mapper.OrderBillMapper;
import com.atguigu.daijia.order.mapper.OrderInfoMapper;
import com.atguigu.daijia.order.mapper.OrderProfitsharingMapper;
import com.atguigu.daijia.order.mapper.OrderStatusLogMapper;
import com.atguigu.daijia.order.service.OrderInfoService;
import com.atguigu.daijia.order.service.OrderMonitorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {


    private final OrderInfoMapper orderInfoMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;
    private final RedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final OrderMonitorService orderMonitorService;
    private final OrderBillMapper orderBillMapper;
    private final OrderProfitsharingMapper orderProfitsharingMapper;

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Long saveOrderInfo(OrderInfoForm orderInfoForm) {
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderInfoForm, orderInfo);
        String orderNo = UUID.randomUUID().toString().replaceAll("-", ""); // 生成订单id
        orderInfo.setStatus(OrderStatus.WAITING_ACCEPT.getStatus());
        orderInfo.setOrderNo(orderNo);
        orderInfoMapper.insert(orderInfo);

        // 记录日志
        this.log(orderInfo.getId(), orderInfo.getStatus());

        //生成订单之后，发送延迟消息
        this.sendDelayMessage(orderInfo.getId());

        // TODO (JIA,2024/8/22,21:09) 亮点四：司机抢单（1、乘客下单那将订单标识先保存至redis）
        // 接单标识，标识不存在了说明不在等待接单状态了
        redisTemplate.opsForValue().set(
                RedisConstant.ORDER_ACCEPT_MARK + orderInfo.getId(),
                "0",
                RedisConstant.ORDER_ACCEPT_MARK_EXPIRES_TIME,
                TimeUnit.MINUTES);
        return orderInfo.getId();
    }

    /**
     * 发送延迟消息
     *
     * @param orderId 订单ID 这是一个长整型的订单标识符，用于唯一标识一个订单
     */
    private void sendDelayMessage(Long orderId) {
        // TODO (JIA,2024/8/31,12:12) 亮点八：使用mq的ttl+死信队列实现订单的超时取消（延迟消息发送）
        try{
            //1 创建队列
            RBlockingQueue<Object> blockingDueue = redissonClient.getBlockingQueue("queue_cancel");

            //2 把创建队列放到延迟队列里面
            RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingDueue);

            //3 发送消息到延迟队列里面
            //设置过期时间
            delayedQueue.offer(orderId.toString(),15,TimeUnit.MINUTES);

        }catch (Exception e) {
            e.printStackTrace();
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }


    @Override
    public Integer getOrderStatus(Long orderId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, orderId);
        queryWrapper.select(OrderInfo::getStatus);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        if (null == orderInfo) {
            // 返回null，feign解析会抛出异常，给默认值，后续会用
            return OrderStatus.NULL_ORDER.getStatus();
        }
        return orderInfo.getStatus();
    }

    /**
     * 记录订单日志
     */
    public void log(Long orderId, Integer status) {
        OrderStatusLog orderStatusLog = new OrderStatusLog();
        orderStatusLog.setOrderId(orderId);
        orderStatusLog.setOrderStatus(status);
        orderStatusLog.setOperateTime(new Date());
        orderStatusLogMapper.insert(orderStatusLog);
    }


    @Override
    public Boolean robNewOrder(Long driverId, Long orderId) {
        // TODO (JIA,2024/8/22,21:09) 亮点四：司机抢单（2、司机通过redis获取订单信息并分布式锁实现抢单最后删除订单标识）
        // 抢单成功或取消订单，都会删除该key，redis判断，减少数据库压力

        if (Boolean.FALSE.equals(redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK + orderId))) {
            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        }

        // 创建锁
        /*
        RLock lock = redissonClient.getLock(RedisConstant.ROB_NEW_ORDER_LOCK + orderId);

        // 获取锁
        try {
            boolean flag = lock.tryLock(
                    RedisConstant.ROB_NEW_ORDER_LOCK_WAIT_TIME,
                    RedisConstant.ROB_NEW_ORDER_LOCK_LEASE_TIME,
                    TimeUnit.SECONDS
            );

            if (flag) {
                // 做一个dcl
                if (Boolean.FALSE.equals(redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK + orderId))) {
                    throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
                }

                // 修改订单状态及司机id
                // update order_info set status = 2, driver_id = #{driverId}, accept_time = now() where id = #{id} and status = 1
                LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getId, orderId)
                        .eq(OrderInfo::getStatus, OrderStatus.WAITING_ACCEPT);

                // 修改字段
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setId(orderId);
                orderInfo.setStatus(OrderStatus.ACCEPTED.getStatus());
                orderInfo.setAcceptTime(new Date());
                orderInfo.setDriverId(driverId);

                int rows = orderInfoMapper.update(orderInfo, wrapper);
                if (rows != 1) {
                    // 抢单失败
                    throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
                }

                // 记录日志
                this.log(orderId, orderInfo.getStatus());

                // 删除redis订单标识
                redisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK + orderId);
            }
        } catch (InterruptedException e) {
            // 抢单失败
            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        } finally {
            // 释放
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        */

        // TODO (jyafoo,2024/10/7,10:28) 司机抢单优化：将抢单改为订单直接推送，然后司机直接接单即可
        // 修改订单状态及司机id
        // update order_info set status = 2, driver_id = #{driverId}, accept_time = now() where id = #{id} and status = 1
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId)
                .eq(OrderInfo::getStatus, OrderStatus.WAITING_ACCEPT);

        // 修改字段
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setStatus(OrderStatus.ACCEPTED.getStatus());
        orderInfo.setAcceptTime(new Date());
        orderInfo.setDriverId(driverId);

        int rows = orderInfoMapper.update(orderInfo, wrapper);
        if (rows != 1) {
            // 抢单失败
            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        }

        // 记录日志
        this.log(orderId, orderInfo.getStatus());

        // 删除redis订单标识
        redisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK + orderId);

        return true;
    }

    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getCustomerId, customerId);

        //乘客端支付完订单，乘客端主要流程就走完（当前这些节点，乘客端会调整到相应的页面处理逻辑）
        Integer[] statusArray = {
                OrderStatus.ACCEPTED.getStatus(),
                OrderStatus.DRIVER_ARRIVED.getStatus(),
                OrderStatus.UPDATE_CART_INFO.getStatus(),
                OrderStatus.START_SERVICE.getStatus(),
                OrderStatus.END_SERVICE.getStatus(),
                OrderStatus.UNPAID.getStatus()
        };

        queryWrapper.in(OrderInfo::getStatus, statusArray);
        queryWrapper.orderByDesc(OrderInfo::getId);
        queryWrapper.last("limit 1"); // 获取最新的一条记录
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        // 封装返回结果
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        if(null != orderInfo) {
            currentOrderInfoVo.setStatus(orderInfo.getStatus());
            currentOrderInfoVo.setOrderId(orderInfo.getId());
            currentOrderInfoVo.setIsHasCurrentOrder(true);
        } else {
            currentOrderInfoVo.setIsHasCurrentOrder(false);
        }

        return currentOrderInfoVo;
    }

    @Override
    public CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getDriverId, driverId);
        //司机发送完账单，司机端主要流程就走完（当前这些节点，司机端会调整到相应的页面处理逻辑）
        Integer[] statusArray = {
                OrderStatus.ACCEPTED.getStatus(),
                OrderStatus.DRIVER_ARRIVED.getStatus(),
                OrderStatus.UPDATE_CART_INFO.getStatus(),
                OrderStatus.START_SERVICE.getStatus(),
                OrderStatus.END_SERVICE.getStatus()
        };
        queryWrapper.in(OrderInfo::getStatus, statusArray);
        queryWrapper.orderByDesc(OrderInfo::getId);
        queryWrapper.last("limit 1");
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        // 封装返回结果
        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();
        if(null != orderInfo) {
            currentOrderInfoVo.setStatus(orderInfo.getStatus());
            currentOrderInfoVo.setOrderId(orderInfo.getId());
            currentOrderInfoVo.setIsHasCurrentOrder(true);
        } else {
            currentOrderInfoVo.setIsHasCurrentOrder(false);
        }

        return currentOrderInfoVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean driverArriveStartLocation(Long orderId, Long driverId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, orderId);
        queryWrapper.eq(OrderInfo::getDriverId, driverId);

        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.DRIVER_ARRIVED.getStatus());
        updateOrderInfo.setArriveTime(new Date());

        //只能更新自己的订单
        int row = orderInfoMapper.update(updateOrderInfo, queryWrapper);
        if(row == 1) {
            //记录日志
            this.log(orderId, OrderStatus.DRIVER_ARRIVED.getStatus());
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateOrderCart(UpdateOrderCartForm updateOrderCartForm) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, updateOrderCartForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, updateOrderCartForm.getDriverId());

        OrderInfo updateOrderInfo = new OrderInfo();
        BeanUtils.copyProperties(updateOrderCartForm, updateOrderInfo);
        updateOrderInfo.setStatus(OrderStatus.UPDATE_CART_INFO.getStatus());
        //只能更新自己的订单
        int row = orderInfoMapper.update(updateOrderInfo, queryWrapper);

        if(row == 1) {
            //记录日志
            this.log(updateOrderCartForm.getOrderId(), OrderStatus.UPDATE_CART_INFO.getStatus());
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean startDrive(StartDriveForm startDriveForm) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, startDriveForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, startDriveForm.getDriverId());

        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.START_SERVICE.getStatus());
        updateOrderInfo.setStartServiceTime(new Date());
        //只能更新自己的订单
        int row = orderInfoMapper.update(updateOrderInfo, queryWrapper);

        if(row == 1) {
            //记录日志
            this.log(startDriveForm.getOrderId(), OrderStatus.START_SERVICE.getStatus());
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }

        //初始化订单监控统计数据
        OrderMonitor orderMonitor = new OrderMonitor();
        orderMonitor.setOrderId(startDriveForm.getOrderId());
        orderMonitorService.saveOrderMonitor(orderMonitor);

        return true;
    }

    @Override
    public Long getOrderNumByTime(String startTime, String endTime) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(OrderInfo::getStartServiceTime, startTime);
        queryWrapper.lt(OrderInfo::getStartServiceTime, endTime);
        return orderInfoMapper.selectCount(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean endDrive(UpdateOrderBillForm updateOrderBillForm) {
        //更新订单信息
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, updateOrderBillForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, updateOrderBillForm.getDriverId());
        //更新字段
        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.END_SERVICE.getStatus());
        updateOrderInfo.setRealAmount(updateOrderBillForm.getTotalAmount());
        updateOrderInfo.setFavourFee(updateOrderBillForm.getFavourFee());
        updateOrderInfo.setEndServiceTime(new Date());
        updateOrderInfo.setRealDistance(updateOrderBillForm.getRealDistance());
        //只能更新自己的订单
        int row = orderInfoMapper.update(updateOrderInfo, queryWrapper);
        if(row == 1) {
            //记录日志
            this.log(updateOrderBillForm.getOrderId(), OrderStatus.END_SERVICE.getStatus());

            // 插入实际账单数据
            OrderBill orderBill = new OrderBill();
            BeanUtils.copyProperties(updateOrderBillForm, orderBill);
            orderBill.setOrderId(updateOrderBillForm.getOrderId());
            orderBill.setPayAmount(orderBill.getTotalAmount());
            orderBillMapper.insert(orderBill);

            // 插入分账信息数据
            OrderProfitsharing orderProfitsharing = new OrderProfitsharing();
            BeanUtils.copyProperties(updateOrderBillForm, orderProfitsharing);
            orderProfitsharing.setOrderId(updateOrderBillForm.getOrderId());
            orderProfitsharing.setRuleId(updateOrderBillForm.getProfitsharingRuleId());
            orderProfitsharing.setStatus(1);
            orderProfitsharingMapper.insert(orderProfitsharing);
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }
        return true;
    }

    @Override
    public PageVo findCustomerOrderPage(Page<OrderInfo> pageParam, Long customerId) {
        IPage<OrderListVo> pageInfo = orderInfoMapper.selectCustomerOrderPage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public PageVo findDriverOrderPage(Page<OrderInfo> pageParam, Long driverId) {
        IPage<OrderListVo> pageInfo = orderInfoMapper.selectDriverOrderPage(pageParam, driverId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public OrderBillVo getOrderBillInfo(Long orderId) {
        OrderBill orderBill = orderBillMapper.selectOne(new LambdaQueryWrapper<OrderBill>().eq(OrderBill::getOrderId, orderId));
        OrderBillVo orderBillVo = new OrderBillVo();
        BeanUtils.copyProperties(orderBill, orderBillVo);
        return orderBillVo;
    }

    @Override
    public OrderProfitsharingVo getOrderProfitsharing(Long orderId) {
        OrderProfitsharing orderProfitsharing = orderProfitsharingMapper.selectOne(new LambdaQueryWrapper<OrderProfitsharing>().eq(OrderProfitsharing::getOrderId, orderId));
        OrderProfitsharingVo orderProfitsharingVo = new OrderProfitsharingVo();
        BeanUtils.copyProperties(orderProfitsharing, orderProfitsharingVo);
        return orderProfitsharingVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean sendOrderBillInfo(Long orderId, Long driverId) {
        //更新订单信息
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, orderId);
        queryWrapper.eq(OrderInfo::getDriverId, driverId);
        //更新字段：完成订单，变成待付款
        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.UNPAID.getStatus());
        //只能更新自己的订单
        int row = orderInfoMapper.update(updateOrderInfo, queryWrapper);
        if(row == 1) {
            //记录日志
            this.log(orderId, OrderStatus.UNPAID.getStatus());
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }
        return true;
    }

    @Override
    public OrderPayVo getOrderPayVo(String orderNo, Long customerId) {
        OrderPayVo orderPayVo = orderInfoMapper.selectOrderPayVo(orderNo, customerId);
        if(null != orderPayVo) {
            String content = orderPayVo.getStartLocation() + " 到 " + orderPayVo.getEndLocation();
            orderPayVo.setContent(content);
        }
        return orderPayVo;
    }

    @Override
    public Boolean updateOrderPayStatus(String orderNo) {
        //1 根据订单编号查询，判断订单状态
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderNo,orderNo);
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
        if(orderInfo == null || orderInfo.getStatus() == OrderStatus.PAID.getStatus()) {
            return true;
        }

        //2 更新状态
        LambdaQueryWrapper<OrderInfo> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(OrderInfo::getOrderNo,orderNo);

        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.PAID.getStatus());
        updateOrderInfo.setPayTime(new Date());

        int rows = orderInfoMapper.update(updateOrderInfo, updateWrapper);

        if(rows == 1) {
            return true;
        } else {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }
    }

    @Override
    public OrderRewardVo getOrderRewardFee(String orderNo) {
        //根据订单编号查询订单表
        OrderInfo orderInfo =
                orderInfoMapper.selectOne(
                        new LambdaQueryWrapper<OrderInfo>()
                                .eq(OrderInfo::getOrderNo, orderNo)
                                .select(OrderInfo::getId,OrderInfo::getDriverId));

        //根据订单id查询系统奖励表
        OrderBill orderBill =
                orderBillMapper.selectOne(new LambdaQueryWrapper<OrderBill>()
                        .eq(OrderBill::getOrderId, orderInfo.getId())
                        .select(OrderBill::getRewardFee));

        //封装到vo里面
        OrderRewardVo orderRewardVo = new OrderRewardVo();
        orderRewardVo.setOrderId(orderInfo.getId());
        orderRewardVo.setDriverId(orderInfo.getDriverId());
        orderRewardVo.setRewardFee(orderBill.getRewardFee());
        return orderRewardVo;
    }


    @Override
    public void orderCancel(long orderId) {
        // TODO (JIA,2024/8/31,12:12) 亮点八：使用mq的ttl+死信队列实现订单的超时取消（延迟消息消费）
        //orderId查询订单信息
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        //判断
        if(Objects.equals(orderInfo.getStatus(), OrderStatus.WAITING_ACCEPT.getStatus())) {
            //修改订单状态：取消状态
            orderInfo.setStatus(OrderStatus.CANCEL_ORDER.getStatus());
            int rows = orderInfoMapper.updateById(orderInfo);
            if(rows == 1) {
                //删除接单标识
                redisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateCouponAmount(Long orderId, BigDecimal couponAmount) {
        int row = orderBillMapper.updateCouponAmount(orderId, couponAmount);
        if(row != 1) {
            throw new GuiguException(ResultCodeEnum.UPDATE_ERROR);
        }
        return true;
    }

}
