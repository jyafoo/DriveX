package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.LoginCheck;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "司机API接口管理")
@RestController
@RequestMapping(value="/driver")
@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
public class DriverController {


    private final DriverService driverService;

    /**
     * 小程序授权登录接口
     *
     * @param code 小程序端传入的登录授权码，用于识别用户身份
     * @return 访问令牌字符串
     */
    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<String> login(@PathVariable String code) {
        return Result.ok(driverService.login(code));
    }

    /**
     * 获取司机登录信息
     */
    @Operation(summary = "获取司机登录信息")
    @LoginCheck
    @GetMapping("/getDriverLoginInfo")
    public Result<DriverLoginVo> getDriverLoginInfo() {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.getDriverLoginInfo(driverId));
    }

    /**
     * 获取司机的认证信息
     *
     * @return 司机认证信息的结果对象
     */
    @Operation(summary = "获取司机认证信息")
    @LoginCheck
    @GetMapping("/getDriverAuthInfo")
    public Result<DriverAuthInfoVo> getDriverAuthInfo() {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.getDriverAuthInfo(driverId));
    }

    /**
     * 更新司机认证信息接口
     * 该接口用于更新司机的认证信息，如驾驶证、行驶证等
     *
     * @param updateDriverAuthInfoForm 司机认证信息更新表单，包含需要更新的各项认证信息
     * @return Boolean值，表示更新是否成功
     */
    @Operation(summary = "更新司机认证信息")
    @LoginCheck
    @PostMapping("/updateDriverAuthInfo")
    public Result<Boolean> updateDriverAuthInfo(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        updateDriverAuthInfoForm.setDriverId(AuthContextHolder.getUserId());
        return Result.ok(driverService.updateDriverAuthInfo(updateDriverAuthInfoForm));
    }


    /**
     * 创建司机人脸模型接口
     *
     * @param driverFaceModelForm 司机人脸模型表单数据，包含司机人脸信息及其他必要数据
     * @return Boolean值，表示人脸模型创建是否成功
     */
    @Operation(summary = "创建司机人脸模型")
    @LoginCheck
    @PostMapping("/creatDriverFaceModel")
    public Result<Boolean> creatDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm) {
        driverFaceModelForm.setDriverId(AuthContextHolder.getUserId());
        return Result.ok(driverService.creatDriverFaceModel(driverFaceModelForm));
    }

}

