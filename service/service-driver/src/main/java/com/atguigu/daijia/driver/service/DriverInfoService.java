package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.entity.driver.DriverInfo;
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
}
