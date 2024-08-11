package com.atguigu.daijia.customer.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 客户信息远程调用接口
 */
@FeignClient(value = "service-customer") // 与service-customer服务进行通信，获取客户信息相关操作
public interface CustomerInfoFeignClient {

    /**
     * 通过指定的授权码登录并获取客户ID
     *
     * @param code 授权码，用于登录验证
     * @return 包含客户ID的结果对象，如果登录成功则返回客户ID，否则返回错误信息
     */
    @GetMapping("/customer/info/login/{code}")
    Result<Long> login(@PathVariable String code);


    /**
     * 获取客户登录信息
     * @param customerId
     * @return
     */
    @GetMapping("/customer/info/getCustomerLoginInfo/{customerId}")
    Result<CustomerLoginVo> getCustomerLoginInfo(@PathVariable("customerId") Long customerId);
}

