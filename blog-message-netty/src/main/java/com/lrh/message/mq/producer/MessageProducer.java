package com.lrh.message.mq.producer;

import com.lrh.message.constants.RedisKeyConstant;
import com.lrh.message.model.MessageModel;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.service.impl.ThreadPoolService;
import com.lrh.message.utils.MessageUtil;
import com.lrh.message.utils.NettyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Value("${rocketmq.remote.consumer.topic}")
    private String remoteTopic;

    @Value("${rocketmq.consumer.topic}")
    private String messageTopic;

    private final ThreadPoolService threadPoolService;

    private final RocketMQTemplate rocketMQTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    public MessageProducer(ThreadPoolService threadPoolService, RocketMQTemplate rocketMQTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.threadPoolService = threadPoolService;
        this.rocketMQTemplate = rocketMQTemplate;
        this.redisTemplate = redisTemplate;
    }

    public void syncSendMessage(MessageModel messageModel) {
        try {
            rocketMQTemplate.syncSend(messageTopic, messageModel);
            log.info("[MessageProducer] 成功发送消息到 Topic: {}, 消息模型: {}", messageTopic, messageModel);
        } catch (Exception e) {
            log.error("[MessageProducer] 发送消息失败，消息模型：{}", messageModel, e);
        }
    }

    public void syncRemoteMessage(MessageDTO messageDTO) {
        String address = (String) redisTemplate.opsForHash()
                .get(RedisKeyConstant.USERID_NETTY_HASH_KEY, messageDTO.getToUserId());
        if (address == null || address.isEmpty()) {
            log.info("[MessageProducer] 用户不在线");
            return;
        }
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(RedisKeyConstant.NETTY_SERVER_SET_KEY, address))) {
            String destTopic = NettyUtil.getDestTopic(address, remoteTopic);
            rocketMQTemplate.syncSend(destTopic, messageDTO);
            log.info("[MessageProducer] 成功发送消息到 Topic: {}, 消息: {}", destTopic, messageDTO);
        } else {
            threadPoolService.setNoOnlineMessageCache(MessageUtil.convertMessageDTOToMessageModel(messageDTO));
            log.info("[MessageProducer] 用户不在线:{}", messageDTO.getToUserId());
        }
    }

}
