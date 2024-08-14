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
    /**
     * 秘钥ID，用于身份验证
     */
    private String secretId;
    /**
     * 秘钥Key，用于身份验证
     */
    private String secretKey;
    /**
     * 区域，用于指定服务的地理区域
     */
    private String region;
    /**
     * 私有存储桶名称，用于存储私有数据
     */
    private String bucketPrivate;
    /**
     * 人员库id
     */
    private String personGroupId;

}