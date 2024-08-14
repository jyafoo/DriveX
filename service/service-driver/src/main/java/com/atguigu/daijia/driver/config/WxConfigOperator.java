package com.atguigu.daijia.driver.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 微信配置操作类
 * <p>
 * 创建和配置微信服务相关的对象
 * @author jyafoo
 * @version 1.0
 * @since 2024/8/10
 */
@Component
public class WxConfigOperator {
    @Autowired
    private WxConfigProperties wxConfigProperties;

    /**
     * 配置并创建微信服务对象
     *
     * @return 配置好的微信服务对象
     */
    @Bean
    public WxMaService wxMaService(){
        // 初始化微信服务默认配置对象
        WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();
        // 设置微信AppID和AppSecret
        wxMaDefaultConfig.setAppid(wxConfigProperties.getAppId());
        wxMaDefaultConfig.setSecret(wxConfigProperties.getSecret());

        // 创建微信服务实现对象
        WxMaServiceImpl wxMaService = new WxMaServiceImpl();
        // 设置微信服务配置
        wxMaService.setWxMaConfig(wxMaDefaultConfig);

        // 返回配置好的微信服务对象
        return wxMaService;
    }
}
