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


    Long submitOrder(SubmitOrderForm submitOrderForm);
}
