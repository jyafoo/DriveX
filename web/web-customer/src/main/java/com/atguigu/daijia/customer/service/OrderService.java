package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.driver.DriverInfoVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;

public interface OrderService {



    /**
     * 预估订单数据
     *
     * @param expectOrderForm 用户下单的所有必要信息
     * @return ExpectOrderVo 用户期望生成的订单信息
     */
    ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm);


    /**
     * 提交订单
     * @param submitOrderForm 订单表单
     * @return 订单id
     */
    Long submitOrder(SubmitOrderForm submitOrderForm);

    /**
     * 查询订单状态
     * 该方法需要用户登录权限，用于查询指定订单的状态
     *
     * @param orderId 订单ID，用于标识要查询的订单
     * @return 订单状态
     */
    Integer getOrderStatus(Long orderId);

    /**
     * 根据乘客ID查询当前正在进行的订单信息
     *
     * @param customerId 乘客的唯一标识ID
     * @return 返回当前订单的信息对象封装在Result中
     */
    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);

    /**
     * 乘客端获取订单信息
     * 该方法需要用户登录认证
     *
     * @param orderId 订单ID
     * @return 返回订单信息的封装对象
     */
    OrderInfoVo getOrderInfo(Long orderId, Long customerId);

    /**
     * 根据订单id获取司机基本信息
     * 该方法需要用户登录后才能访问，返回指定订单的司机信息
     *
     * @param orderId 订单ID，用于识别特定的订单
     * @return 返回DriverInfoVo对象，包含司机的基本信息
     */
    DriverInfoVo getDriverInfo(Long orderId, Long customerId);
}
