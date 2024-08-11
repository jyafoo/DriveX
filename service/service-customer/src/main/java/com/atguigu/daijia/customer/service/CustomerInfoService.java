package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CustomerInfoService extends IService<CustomerInfo> {

    /**
     * 小程序授权登录
     *
     * @param code 微信登录接口调用后返回的code，用于换取access_token
     * @return 包含用户ID的Result对象，表示登录结果
     */
    Long login(String code);

    /**
     * 根据客户ID获取客户登录信息
     *
     * @param customerId 客户ID，用于标识特定的客户
     * @return CustomerLoginVo对象
     */
    CustomerLoginVo getCustomerLoginInfo(Long customerId);
}
