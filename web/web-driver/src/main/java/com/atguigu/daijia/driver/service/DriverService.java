package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;

public interface DriverService {


    /**
     * 小程序授权登录接口
     *
     * @param code 小程序端传入的登录授权码，用于识别用户身份
     * @return 访问令牌字符串
     */
    String login(String code);

    /**
     * 获取司机登录信息
     */
    DriverLoginVo getDriverLoginInfo(Long driverId);

    /**
     * 获取司机的认证信息
     *
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
     *
     * @param driverFaceModelForm 司机人脸模型表单数据，包含司机人脸信息及其他必要数据
     * @return Boolean值，表示人脸模型创建是否成功
     */
    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);

    /**
     * 判断司机当日是否进行过人脸识别
     *
     * @param driverId 司机ID
     * @return 司机是否进行过人脸识别
     */
    Boolean isFaceRecognition(Long driverId);

    /**
     * 判断司机当日是否进行过人脸识别
     *
     * @param driverFaceModelForm
     * @return 司机是否进行过人脸识别
     */
    Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm);

    /**
     * 开始接单服务
     *
     * @param driverId 司机ID
     * @return 操作结果，包含一个布尔值表示更新是否成功
     */
    Boolean startService(Long driverId);

    /**
     * 停止接单服务
     *
     * @return 操作结果，包含一个布尔值表示更新是否成功
     */
    Boolean stopService(Long driverId);
}
