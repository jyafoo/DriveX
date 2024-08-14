package com.atguigu.daijia.driver.service;

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
}
