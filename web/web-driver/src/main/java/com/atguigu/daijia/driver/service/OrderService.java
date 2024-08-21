package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.order.NewOrderDataVo;

import java.util.List;

public interface OrderService {


    /**
     * 查询订单状态
     *
     * @param orderId 订单ID，用于标识要查询的订单
     * @return 订单状态
     */
    Integer getOrderStatus(Long orderId);

    /**
     * 根据司机ID查询新的订单队列数据
     *
     * @param driverId 司机的ID
     * @return 新订单数据的列表，订单数据与指定的司机相关
     */
    List<NewOrderDataVo> findNewOrderQueueData(Long driverId);
}
