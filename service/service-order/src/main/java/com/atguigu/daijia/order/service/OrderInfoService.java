package com.atguigu.daijia.order.service;

import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 保存订单信息
     *
     * @param orderInfoForm 订单信息表单对象，包含要保存的订单信息
     * @return 返回保存结果，包括状态码和消息，以及保存的订单ID
     */
    Long saveOrderInfo(OrderInfoForm orderInfoForm);

    /**
     * 根据订单id获取订单状态
     *
     * @param orderId 订单的唯一标识符，用于查询订单状态
     * @return 订单状态
     */
    Integer getOrderStatus(Long orderId);

    /**
     * 司机抢单接口
     *
     * @param driverId 抢单司机的唯一标识ID
     * @param orderId 待抢订单的唯一标识ID
     * @return 抢单操作是否成功的标识
     */
    Boolean robNewOrder(Long driverId, Long orderId);

    /**
     * 根据乘客ID查询当前正在进行的订单信息
     *
     * @param customerId 乘客的唯一标识ID
     * @return 返回当前订单的信息对象封装在Result中
     */
    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);

    /**
     * 根据司机ID查询当前正在进行的订单详情
     *
     * @param driverId 司机的唯一标识ID
     * @return 返回当前订单的信息封装在Result对象中
     */
    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    /**
     * 司机到达起始点接口
     * 该接口用于记录司机到达订单起始点的状态
     *
     * @param orderId 订单ID
     * @param driverId 司机ID
     * @return 返回一个结果对象，包含一个布尔值，指示司机是否成功到达起始点
     */
    Boolean driverArriveStartLocation(Long orderId, Long driverId);
}
