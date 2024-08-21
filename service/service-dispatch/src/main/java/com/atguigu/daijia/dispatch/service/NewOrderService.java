package com.atguigu.daijia.dispatch.service;

import com.atguigu.daijia.model.vo.dispatch.NewOrderTaskVo;

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
}
