package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.service.DriverInfoService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 更新司机认证信息接口
     * 该接口用于更新司机的认证信息，如驾驶证、行驶证等
     *
     * @param updateDriverAuthInfoForm 司机认证信息更新表单，包含需要更新的各项认证信息
     * @return Boolean值，表示更新是否成功
     */
    @Operation(summary = "更新司机认证信息")
    @PostMapping("/updateDriverAuthInfo")
    public Result<Boolean> UpdateDriverAuthInfo(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        return Result.ok(driverInfoService.updateDriverAuthInfo(updateDriverAuthInfoForm));
    }

    /**
     * 创建司机人脸模型接口
     * 用于新人脸模型的创建，以便于后续的人脸识别操作
     *
     * @param driverFaceModelForm 包含司机人脸模型信息的请求体
     * @return 布尔值，表示人脸模型创建是否成功
     */
    @Operation(summary = "创建司机人脸模型")
    @PostMapping("/creatDriverFaceModel")
    public Result<Boolean> creatDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm) {
        return Result.ok(driverInfoService.creatDriverFaceModel(driverFaceModelForm));
    }

    /**
     * 根据司机ID获取司机设置信息
     *
     * @param driverId 司机ID，用于标识特定的司机
     * @return DriverSet对象
     */
    @Operation(summary = "获取司机设置信息")
    @GetMapping("/getDriverSet/{driverId}")
    public Result<DriverSet> getDriverSet(@PathVariable Long driverId) {
        return Result.ok(driverInfoService.getDriverSet(driverId));
    }

    /**
     * 判断司机当日是否进行过人脸识别
     *
     * @param driverId 司机ID
     * @return 司机是否进行过人脸识别
     */
    @Operation(summary = "判断司机当日是否进行过人脸识别")
    @GetMapping("/isFaceRecognition/{driverId}")
    Result<Boolean> isFaceRecognition(@PathVariable("driverId") Long driverId) {
        return Result.ok(driverInfoService.isFaceRecognition(driverId));
    }

    /**
     * 验证司机的人脸信息
     *
     * @param driverFaceModelForm 司机人脸模型数据表单
     * @return 布尔类型，表示人脸验证是否成功
     */
    @Operation(summary = "验证司机人脸")
    @PostMapping("/verifyDriverFace")
    public Result<Boolean> verifyDriverFace(@RequestBody DriverFaceModelForm driverFaceModelForm) {
        return Result.ok(driverInfoService.verifyDriverFace(driverFaceModelForm));
    }

    /**
     * 更新接单状态
     *
     * @param driverId 司机ID
     * @param status 新的接单状态
     * @return 操作结果，包含一个布尔值表示更新是否成功
     */
    @Operation(summary = "更新接单状态")
    @GetMapping("/updateServiceStatus/{driverId}/{status}")
    public Result<Boolean> updateServiceStatus(@PathVariable Long driverId, @PathVariable Integer status) {
        return Result.ok(driverInfoService.updateServiceStatus(driverId, status));
    }


}

