package com.atguigu.daijia.order.handle;

import com.atguigu.daijia.order.service.OrderInfoService;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component

public class RedisDelayHandle {

    private final RedissonClient redissonClient;

    private final OrderInfoService orderInfoService;

    public RedisDelayHandle(RedissonClient redissonClient, OrderInfoService orderInfoService) {
        this.redissonClient = redissonClient;
        this.orderInfoService = orderInfoService;
    }

    @PostConstruct
    public void listener() {
        // TODO (JIA,2024/8/31,12:12) 亮点八：使用mq的ttl+死信队列实现订单的超时取消（延迟消息监听）
        new Thread(()->{
            while(true) {
                // 获取延迟队列里面阻塞队列
                RBlockingQueue<String> blockingQueue = redissonClient.getBlockingQueue("queue_cancel");

                // 从队列获取消息
                try {
                    String orderId = blockingQueue.take();

                    // 取消订单
                    if(StringUtils.hasText(orderId)) {
                        // 调用方法取消订单
                        orderInfoService.orderCancel(Long.parseLong(orderId));
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }
}