package com.lrh.message.mq.consumer;

import com.lrh.message.model.MessageModel;
import com.lrh.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.mq.consumer
 * @ClassName: MessageConsumer
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 18:57
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.consumer.group}")
public class MessageConsumer implements RocketMQListener<MessageModel> {

    private final MessageService messageService;

    public MessageConsumer(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onMessage(MessageModel messageModel) {
        log.info("[MessageConsumer] 接收到消息: {}", messageModel);
        messageService.getBaseMapper().insert(messageModel);
    }
}
