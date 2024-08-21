package com.atguigu.daijia.dispatch.service;

import com.atguigu.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.atguigu.daijia.model.vo.order.NewOrderDataVo;

import java.util.List;

public interface NewOrderService {

    /**
     * 添加并开始新订单任务调度
     * @param newOrderTaskVo 新订单任务信息
     * @return 调度任务的ID
     */
    Long addAndStartTask(NewOrderTaskVo newOrderTaskVo);

    /**
     * 执行任务：搜索附近代驾司机
     * @param jobId
     */
    void executeTask(long jobId);


    /**
     * 根据司机ID查询新的订单队列数据
     *
     * @param driverId 司机的ID
     * @return 新订单数据的列表，订单数据与指定的司机相关
     */
    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);

    /**
     * 清除新订单队列中的数据
     *
     * @param driverId 司机ID
     * @return 清除操作的结果，true表示成功清除，false表示清除失败
     */
    Boolean clearNewOrderQueueData(Long driverId);
}
