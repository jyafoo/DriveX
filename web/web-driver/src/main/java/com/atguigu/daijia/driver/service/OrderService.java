package com.atguigu.daijia.driver.service;

public interface OrderService {


    /**
     * 查询订单状态
     *
     * @param orderId 订单ID，用于标识要查询的订单
     * @return 订单状态
     */
    Integer getOrderStatus(Long orderId);
}
