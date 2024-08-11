package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;

public interface CustomerService {

    /**
     * 小程序授权登录
     *
     * @param code 微信登录接口调用后返回的code
     * @return token
     */
    String login(String code);

    /**
     * 根据用户id获取客户登录信息
     *
     * @param customerId 用户id
     * @return 返回一个结果对象，包含客户登录信息。结果对象的状态码为200，表示成功获取到客户登录信息
     */
    CustomerLoginVo getCustomerLoginInfo(Long customerId);
}
