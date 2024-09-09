package com.atguigu.daijia.payment.receiver;

import com.atguigu.daijia.common.constant.MqConst;
import com.atguigu.daijia.payment.service.WxPayService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author jyafoo
 * @version 1.0
 * @since 2024/8/30
 */
@Component
public class PaymentReceiver {
    // TODO (JIA,2024/8/30,21:36) 亮点七：支付成功后，使用rabbitmq实现异步存储支付后处理逻辑（消息接收端）
    @Resource
    private WxPayService wxPayService;

    /**
     * 监听支付成功的消息
     *
     * @param orderNo 订单编号，用于标识支付成功的订单
     * @param message 消息内容，封装了支付成功的详细信息
     * @param channel 消息通道，用于与消息中间件进行交互，可在处理消息后进行确认或拒绝
     */
    @RabbitListener(bindings = @QueueBinding(
            // 绑定的队列名称 (MqConst.QUEUE_PAY_SUCCESS) 并设置队列为持久化 (durable = "true")
            value = @Queue(value = MqConst.QUEUE_PAY_SUCCESS,durable = "true"),
            // 绑定的交换机名称 (MqConst.EXCHANGE_ORDER)
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER),
            // 绑定的路由键 (MqConst.ROUTING_PAY_SUCCESS)，用于将消息路由到正确的队列
            key = {MqConst.ROUTING_PAY_SUCCESS}
    ))
    public void paySuccess(String orderNo, Message message, Channel channel) {
        // 调用微信支付服务的订单处理方法，处理支付成功的订单
        wxPayService.handleOrder(orderNo);
    }

}
