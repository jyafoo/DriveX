package com.atguigu.daijia.payment.service;

import com.atguigu.daijia.model.form.payment.PaymentInfoForm;
import com.atguigu.daijia.model.vo.payment.WxPrepayVo;

public interface WxPayService {


    /**
     * 创建微信支付订单
     * 该方法用于生成微信支付的预支付信息，前端根据返回的信息调起支付
     *
     * @param paymentInfoForm 支付信息表单，包含生成支付所需的必要信息
     * @return 返回微信支付的预支付信息包装类，以及成功状态码
     */
    WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm);

    /**
     * 查询支付状态
     * 通过订单号查询支付状态，以便确认用户是否完成支付
     *
     * @param orderNo 订单号，用于标识查询的支付订单
     * @return 返回支付状态的查询结果
     */
    Boolean queryPayStatus(String orderNo);
}
