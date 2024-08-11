package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final RedisTemplate redisTemplate;

    /**
     * 小程序授权登录接口
     *
     * @param code 微信登录凭证，用于换取用户信息
     * @return 返回登录结果，包含用户信息或错误信息
     */
    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<String> wxLogin(@PathVariable String code){
        return Result.ok(customerService.login(code));
    }


    /**
     * 获取客户登录信息
     *
     * @return 返回一个结果对象，包含客户登录信息。结果对象的状态码为200，表示成功获取到客户登录信息
     */
    @LoginCheck // 自定义切面校验用户 token
    @Operation(summary = "获取客户登录信息")
    @GetMapping("/getCustomerLoginInfo")
    public Result<CustomerLoginVo> getCustomerLoginInfo() {
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(customerService.getCustomerLoginInfo(customerId));
    }
    /*@Operation(summary = "获取客户登录信息")
    @GetMapping("/getCustomerLoginInfo")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@RequestHeader(value = "token") String token) {
        String customerId = (String)redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX+token);
        if(StringUtils.isEmpty(customerId)){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return Result.ok(customerService.getCustomerLoginInfo(Long.parseLong(customerId)));
    }*/


    /**
     * 更新用户微信手机号
     *
     * 通过POST请求，使用@Operation注解对API进行描述，summary属性指定了操作的摘要信息
     * 使用@LoginCheck注解确保请求用户已登录
     *
     * @param updateWxPhoneForm 包含更新微信手机号所需信息的表单对象
     * @return 更新结果，使用Result.ok封装返回
     */
    @Operation(summary = "更新用户微信手机号")
    @LoginCheck
    @PostMapping("/updateWxPhone")
    public Result updateWxPhone(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
        updateWxPhoneForm.setCustomerId(AuthContextHolder.getUserId());
        return Result.ok(customerService.updateWxPhoneNumber(updateWxPhoneForm));
    }


}

