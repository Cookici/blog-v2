package com.lrh.message.mq.producer;

import com.lrh.message.constants.RocketMQConstant;
import com.lrh.message.model.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.mq.producer
 * @ClassName: MessageProducer
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 18:56
 */
@Slf4j
@Service
public class MessageProducer {

    private final RocketMQTemplate rocketMQTemplate;

    public MessageProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void syncSendMessage(MessageModel messageModel) {
        try {
            String topic = RocketMQConstant.MESSAGE_TOPIC;
            rocketMQTemplate.syncSend(topic, messageModel);
            log.info("[MessageProducer] 成功发送消息到 Topic: {}, 消息模型: {}", topic, messageModel);
        } catch (Exception e) {
            log.error("[MessageProducer] 发送消息失败，消息模型：{}", messageModel, e);
        }
    }

}
