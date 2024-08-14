package com.atguigu.daijia.driver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信配置属性类
 * <p>
 * 封装微信相关的配置信息：AppID和AppSecret
 * @author jyafoo
 * @version 1.0
 * @since 2024/8/10
 */

@Component
@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxConfigProperties {
    /**
     * 微信AppID，用于标识应用的唯一身份
     */

    private String appId;

    /**
     * 微信AppSecret，用于应用安全验证的密钥
     */
    private String secret;
}
