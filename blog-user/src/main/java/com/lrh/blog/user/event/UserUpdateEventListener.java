package com.lrh.blog.user.event;

import com.lrh.blog.user.dto.event.UserUpdateMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 用户更新事件监听器
 * 负责将用户更新事件转发到RocketMQ
 */
@Slf4j
@Component
public class UserUpdateEventListener {

    private final RocketMQTemplate rocketMQTemplate;
    
    @Value("${rocketmq.producer.topic}")
    private String topic;

    public UserUpdateEventListener(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @EventListener
    public void handleUserUpdateEvent(UserUpdateEvent event) {
        log.info("[UserUpdateEventListener] handleUserUpdateEvent 接收到用户更新事件: userId={}, userName={}", event.getUserId(), event.getUserName());
        
        // 创建消息对象
        UserUpdateMessage message = new UserUpdateMessage();
        message.setUserId(event.getUserId());
        message.setUserName(event.getUserName());
        
        // 发送消息到MQ
        try {
            rocketMQTemplate.convertAndSend(topic, message);
            log.info("[UserUpdateEventListener] handleUserUpdateEvent 用户更新消息发送成功: userId={}, userName={}", message.getUserId(), message.getUserName());
        } catch (Exception e) {
            log.error("[UserUpdateEventListener] handleUserUpdateEvent 用户更新消息发送失败: {}", e.getMessage(), e);
        }
    }
}