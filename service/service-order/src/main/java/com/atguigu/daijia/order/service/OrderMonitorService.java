package com.atguigu.daijia.order.service;

import com.atguigu.daijia.model.entity.order.OrderMonitor;
import com.atguigu.daijia.model.entity.order.OrderMonitorRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderMonitorService extends IService<OrderMonitor> {

    /**
     * 保存订单监控记录数据
     *
     * @param orderMonitorRecord 订单监控记录对象，包含要保存的订单监控数据
     * @return 布尔值，表示保存操作是否成功
     */
    Boolean saveOrderMonitorRecord(OrderMonitorRecord orderMonitorRecord);
}
