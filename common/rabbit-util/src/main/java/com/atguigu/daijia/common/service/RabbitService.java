package com.atguigu.daijia.common.service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定的交换机和路由键
     *
     * @param exchange    消息将要发送的交换机名
     * @param routingkey  消息的路由键，用于消息的路由选择
     * @param message     要发送的消息对象，将被自动转换为Message对象发送
     * @return            返回true，表示消息发送成功
     *
     * 注意：本方法使用RabbitTemplate的convertAndSend方法，该方法会自动序列化对象并发送到MQ中
     */
    public boolean sendMessage(String exchange,
                               String routingkey,
                               Object message) {
        rabbitTemplate.convertAndSend(exchange,routingkey,message);
        return true;
    }

}
