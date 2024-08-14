package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.service.DriverInfoService;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "司机API接口管理")
@RestController
@RequestMapping(value="/driver/info")
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoController {
    @Autowired
    private DriverInfoService driverInfoService;

    /**
     * 小程序授权登录接口
     *
     * @param code 微信登录凭证，用于换取用户的openid
     * @return 用户的唯一标识（UID）
     */
    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<Long> login(@PathVariable String code) {
        return Result.ok(driverInfoService.login(code));
    }

    /**
     * 获取司机登录信息
     *
     * @param driverId 司机ID，用于标识特定的司机
     * @return 司机登录信息
     *         成功时，结果状态码为200，并包含司机登录信息对象；
     *         失败时，结果状态码非200，不包含司机登录信息对象
     */
    @Operation(summary = "获取司机登录信息")
    @GetMapping("/getDriverLoginInfo/{driverId}")
    public Result<DriverLoginVo> getDriverLoginInfo(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverLoginInfo(driverId));
    }

    /**
     * 获取司机的认证信息
     *
     * @param driverId 司机的ID
     * @return 司机认证信息的结果对象
     */
    @Operation(summary = "获取司机认证信息")
    @GetMapping("/getDriverAuthInfo/{driverId}")
    Result<DriverAuthInfoVo> getDriverAuthInfo(@PathVariable("driverId") Long driverId) {
        return Result.ok(driverInfoService.getDriverAuthInfo(driverId));
    }

}

