package com.atguigu.daijia.driver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局自定义线程池配置
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        // 动态获取服务器核数
        int processors = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(
                processors + 1, // 核心线程个数 io:2n ,cpu: n+1  n:内核数据
                processors+1, // 最大线程数同样设置为处理器核心数+1，以限制并发执行的任务数量
                0, // 空闲线程存活时间设置为0，意味着超过核心线程数的线程将立即被终止
                TimeUnit.SECONDS, // 上述存活时间的时间单位为秒
                new ArrayBlockingQueue<>(3), // 使用容量为3的阻塞队列，用于存放待执行的任务
                Executors.defaultThreadFactory(), // 使用默认的线程工厂创建新线程
                new ThreadPoolExecutor.AbortPolicy() // 当任务队列已满且无法再添加新任务时，采用拒绝策略直接抛出异常
        );
    }
}