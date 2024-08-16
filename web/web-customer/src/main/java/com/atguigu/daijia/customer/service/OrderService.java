package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;

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
}
