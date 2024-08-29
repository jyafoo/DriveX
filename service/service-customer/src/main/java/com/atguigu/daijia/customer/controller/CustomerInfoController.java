package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.customer.service.CustomerInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息控制层
 *
 * @author jyafoo
 * @version 1.0
 * @since 2024/8/10
 */

@Slf4j
@RestController
@RequestMapping("/customer/info")
@SuppressWarnings({"unchecked", "rawtypes"}) // 使用SuppressWarnings注解来忽略未检查的类型转换警
@RequiredArgsConstructor
public class CustomerInfoController {

    private final CustomerInfoService customerInfoService;


    /**
     * 根据客户ID获取客户登录信息
     *
     * @param customerId 客户ID，用于标识特定的客户
     * @return CustomerLoginVo对象
     */
    @Operation(summary = "获取客户登录信息")
    @GetMapping("/getCustomerLoginInfo/{customerId}")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@PathVariable Long customerId) {
        return Result.ok(customerInfoService.getCustomerLoginInfo(customerId));
    }

    /**
     * 根据客户ID获取客户基本信息
     *
     * @param customerId 客户的唯一标识ID，从路径中获取
     * @return 包含客户信息的Result对象，其中包含了一个CustomerInfo对象，该对象包含了请求的客户的基本信息
     */
    @Operation(summary = "获取客户基本信息")
    @GetMapping("/getCustomerInfo/{customerId}")
    public Result<CustomerInfo> getCustomerInfo(@PathVariable Long customerId) {
        return Result.ok(customerInfoService.getById(customerId));
    }

    /**
     * 小程序授权登录
     *
     * @param code 微信登录接口调用后返回的code，用于换取access_token
     * @return 包含用户ID的Result对象，表示登录结果
     */
    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<Long> login(@PathVariable String code){
        return Result.ok(customerInfoService.login(code));
    }

    /**
     * 更新客户微信手机号码
     * 该方法接收一个UpdateWxPhoneForm对象，该对象包含需要更新的客户微信手机号码信息，
     * 并委托给customerInfoService进行更新操作
     *
     * @param updateWxPhoneForm 包含微信手机号码更新信息的表单对象
     * @return 返回一个Result对象，其中包含一个Boolean值，表示更新操作是否成功
     */
    @Operation(summary = "更新客户微信手机号码")
    @PostMapping("/updateWxPhoneNumber")
    public Result<Boolean> updateWxPhoneNumber(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
        return Result.ok(customerInfoService.updateWxPhoneNumber(updateWxPhoneForm));
    }

    /**
     * 获取客户OpenId
     *
     * @param customerId 客户的唯一标识ID，通过路径变量获取
     * @return 操作结果或错误信息
     */
    @Operation(summary = "获取客户OpenId")
    @GetMapping("/getCustomerOpenId/{customerId}")
    public Result<String> getCustomerOpenId(@PathVariable Long customerId) {
        return Result.ok(customerInfoService.getCustomerOpenId(customerId));
    }

}

