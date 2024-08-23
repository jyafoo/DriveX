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

    /**
     * 司机抢单接口
     * 此接口允许已登录的司机用户尝试抢夺指定的订单
     * 抢单成功意味着订单状态将被更新，以表示该司机将执行此订单
     *
     * @param orderId 订单ID，用于标识司机想要抢夺的订单
     * @return 抢单操作的结果，包含一个布尔值，true表示抢单成功，false表示抢单失败
     */
    Boolean robNewOrder(Long driverId, Long orderId);
}
