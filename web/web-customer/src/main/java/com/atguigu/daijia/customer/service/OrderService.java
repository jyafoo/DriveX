package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.form.payment.CreateWxPaymentForm;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.driver.DriverInfoVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
import com.atguigu.daijia.model.vo.map.OrderServiceLastLocationVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;
import com.atguigu.daijia.model.vo.payment.WxPrepayVo;

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

    /**
     * 获取代驾订单的经纬度位置
     *
     * @param orderId 订单ID，用于标识特定的订单
     * @return 返回一个Result对象，其中包含订单的位置信息（经度和纬度）
     */
    OrderLocationVo getCacheOrderLocation(Long orderId);

    /**
     * 获取订单服务最后一个位置信息
     * 该方法通过订单ID查询并返回订单服务的最后一个位置信息，主要用于需要实时获取代驾服务位置的场景
     *
     * @param orderId 订单ID，用于标识特定的代驾服务订单
     * @return 订单服务的最后一个位置信息
     */
    OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId);

    /**
     * 获取乘客订单分页列表
     * @param page
     * @param limit
     * @return
     */
    PageVo findCustomerOrderPage(Long customerId, Long page, Long limit);

    /**
     * 创建微信支付信息
     *
     * @param createWxPaymentForm 创建微信支付的表单数据，包含订单金额、商品描述等必要信息
     * @return 返回一个微信预支付的语音对象，其中包含微信支付所需的预支付ID等关键信息
     */
    WxPrepayVo createWxPayment(CreateWxPaymentForm createWxPaymentForm);

    /**
     * 查询支付状态
     *
     * @param orderNo 订单号，用于标识查询的目标订单
     * @return 包含支付状态的Result对象，支付状态为布尔值，true表示支付成功，false表示支付失败或未支付
     */
    Boolean queryPayStatus(String orderNo);
}
