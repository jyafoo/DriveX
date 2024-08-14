package com.atguigu.daijia.driver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 騰訊雲cos配置属性类
 * <p>
 * 封装cos相关的配置信息
 * @author jyafoo
 * @version 1.0
 * @since 2024/8/13
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentCloudProperties {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketPrivate;
}