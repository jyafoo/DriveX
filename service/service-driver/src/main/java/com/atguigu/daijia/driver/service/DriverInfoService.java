package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.entity.driver.DriverInfo;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DriverInfoService extends IService<DriverInfo> {

    /**
     * 小程序授权登录接口
     *
     * @param code 微信登录凭证，用于换取用户的openid
     * @return 用户的唯一标识（UID）
     */
    Long login(String code);

    /**
     * 获取司机登录信息
     *
     * @param driverId 司机ID，用于标识特定的司机
     * @return 司机登录信息
     *         成功时，结果状态码为200，并包含司机登录信息对象；
     *         失败时，结果状态码非200，不包含司机登录信息对象
     */
    DriverLoginVo getDriverLoginInfo(Long driverId);

    /**
     * 获取司机的认证信息
     *
     * @param driverId 司机的ID
     * @return 司机认证信息的结果对象
     */
    DriverAuthInfoVo getDriverAuthInfo(Long driverId);

    /**
     * 更新司机认证信息接口
     * 该接口用于更新司机的认证信息，如驾驶证、行驶证等
     *
     * @param updateDriverAuthInfoForm 司机认证信息更新表单，包含需要更新的各项认证信息
     * @return Boolean值，表示更新是否成功
     */
    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);

    /**
     * 创建司机人脸模型接口
     * 用于新人脸模型的创建，以便于后续的人脸识别操作
     *
     * @param driverFaceModelForm 包含司机人脸模型信息的请求体
     * @return 布尔值，表示人脸模型创建是否成功
     */
    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);

    /**
     * 根据司机ID获取司机设置信息
     *
     * @param driverId 司机ID，用于标识特定的司机
     * @return DriverSet对象
     */
    DriverSet getDriverSet(Long driverId);

    /**
     * 判断司机当日是否进行过人脸识别
     *
     * @param driverId 司机ID
     * @return 司机是否进行过人脸识别
     */
    Boolean isFaceRecognition(Long driverId);
}
