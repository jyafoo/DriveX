package com.atguigu.daijia.common.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Redis配置类
 *
 */
@Configuration
@EnableCaching
public class RedisConfig {



    /**
     * 配置一个自定义的KeyGenerator Bean,用默认标签做缓存
     * 该Bean用于生成唯一标识符，以区分不同的目标对象、方法和参数组合
     * 用于缓存等相关场景，确保缓存项的唯一性
     *
     * @return 自定义的KeyGenerator实现
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            /**
             * 生成唯一标识符的方法
             * 通过拼接目标对象的类名、方法名和参数值来实现唯一性
             *
             * @param target 目标对象
             * @param method 目标方法
             * @param params 方法参数
             * @return 拼接后的唯一标识符字符串
             */
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                // 拼接目标对象的类名
                sb.append(target.getClass().getName());
                // 拼接方法名
                sb.append(method.getName());
                // 遍历并拼接参数值
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                // 返回拼接后的唯一标识符
                return sb.toString();
            }
        };
    }



    /**
     * 配置RedisTemplate Bean
     *
     * @param redisConnectionFactory Redis连接工厂，用于建立Redis连接
     * @return 配置好的RedisTemplate实例
     */
    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建RedisTemplate实例
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 设置Redis连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用StringRedisSerializer来序列化key
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 使用GenericJackson2JsonRedisSerializer来序列化value，替换默认的JDK序列化方式
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        // 为RedisTemplate设置序列化方式
        // 对key使用StringRedisSerializer进行序列化
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 对value使用GenericJackson2JsonRedisSerializer进行序列化
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        // 对Hash的key使用StringRedisSerializer进行序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 对Hash的value使用GenericJackson2JsonRedisSerializer进行序列化
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        // 初始化RedisTemplate，完成所有属性的设置
        redisTemplate.afterPropertiesSet();
        // 返回配置好的RedisTemplate实例
        return redisTemplate;
    }


    /**
     * 配置CacheManager bean以使用自定义的缓存管理策略
     * 主要用于解决缓存数据的序列化和反序列化问题，以及设置缓存的默认过期时间
     *
     * @param factory Redis连接工厂，用于建立Redis连接
     * @return 配置好的CacheManager实例
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 使用StringRedisSerializer来序列化和反序列化缓存的键
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化缓存的值
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        // 配置ObjectMapper来解决查询缓存转换异常的问题
        // 允许以任意方式访问属性，并启用默认类型，以支持多态
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 设置自定义的ObjectMapper到Jackson2JsonRedisSerializer
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置Redis缓存条目的序列化方式和过期时间
        // 设置缓存值的默认过期时间为600秒，并使用自定义的序列化器
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600000))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        // 构建RedisCacheManager，设置默认的缓存配置
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }
}
