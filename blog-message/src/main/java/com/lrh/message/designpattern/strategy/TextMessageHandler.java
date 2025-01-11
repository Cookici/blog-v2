package com.lrh.message.designpattern.strategy;

import com.alibaba.fastjson2.JSON;
import com.lrh.common.util.IdUtil;
import com.lrh.message.config.designpattern.strategy.AbstractExecuteStrategy;
import com.lrh.message.enums.MessageTypeEnum;
import com.lrh.message.model.MessageModel;
import com.lrh.message.mq.producer.MessageProducer;
import com.lrh.message.websocket.MessageDTO;
import com.lrh.message.websocket.MessageHandler;
import com.lrh.message.websocket.MessageVO;
import com.lrh.message.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.designpattern.strategy
 * @ClassName: TextMessageProcessHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 00:35
 */
@Slf4j
@Service
public class TextMessageHandler extends AbstractMessageHandler implements AbstractExecuteStrategy<MessageHandler, Void> {

    private final MessageProducer messageProducer;

    private final RedisTemplate<String, Object> redisTemplate;

    public TextMessageHandler(MessageProducer messageProducer, RedisTemplate<String, Object> redisTemplate) {
        this.messageProducer = messageProducer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void processMessage(MessageHandler messageHandler) {
        MessageDTO messageDTO = messageHandler.getMessageDTO();
        WebSocketServer webSocketServer = messageHandler.getSocketMap().get(messageDTO.getToUserId());
        MessageModel messageModel = convertMessageDTOToMessageModel(messageDTO);
        if (webSocketServer == null) {
            log.info("[WebSocketServer] 用户: {} 不在线", messageDTO.getToUserId());
            return;
        }
        try {
            MessageVO message = new MessageVO();
            message.setMessageType(messageDTO.getMessageType());
            message.setMessageContent(messageDTO.getMessageContent());
            setRedis(messageModel);
            webSocketServer.getSession().getBasicRemote().sendText(JSON.toJSONString(message));
        } catch (RuntimeException | IOException e) {
            log.error("[TextMessageProcessHandler] processMessage error: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        executorService.execute(() -> {
            messageProducer.syncSendMessage(messageModel);
        });
    }

    @Override
    public String mark() {
        return MessageTypeEnum.TextMessage.getMessageType();
    }

    @Override
    public void execute(MessageHandler messageHandler) {
        processMessage(messageHandler);
    }

    /**
     * ARGV[1]: ZSet 分值 (timestamp)
     * ARGV[2]: ZSet 数据 (序列化的 MessageModel)
     * ARGV[3]: 过期时间 (秒)
     *
     * @param userId       发送者id
     * @param toUserId     接收者id
     * @param messageModel 消息模型
     */
    @Override
    protected void setRedis(MessageModel messageModel) {
        String redisKey = IdUtil.getMessageOneToOneRedisKey(messageModel.getUserId(), messageModel.getToUserId());
        String luaScript =
                "redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2]) " +
                        "redis.call('EXPIRE', KEYS[1], ARGV[3])";

        List<String> keys = Collections.singletonList(redisKey);
        List<Object> args = Arrays.asList(
                messageModel.getTimestamp(),
                messageModel,
                Duration.ofDays(7).getSeconds()
        );

        RedisScript<Void> redisScript = new DefaultRedisScript<>(luaScript, Void.class);
        redisTemplate.execute(redisScript, keys, args.toArray());
        log.info("[TextMessageProcessHandler] setRedis {},redis缓存成功", messageModel);
    }

    private MessageModel convertMessageDTOToMessageModel(MessageDTO messageDTO) {
        MessageModel messageModel = new MessageModel();
        messageModel.setMessageContent(messageDTO.getMessageContent());
        messageModel.setMessageType(messageDTO.getMessageType());
        messageModel.setUserId(messageDTO.getUserId());
        messageModel.setToUserId(messageDTO.getToUserId());
        messageModel.setTimestamp(messageDTO.getTimestamp());
        messageModel.setMessageId("message_" + IdUtil.getUuid());
        return messageModel;
    }


}
