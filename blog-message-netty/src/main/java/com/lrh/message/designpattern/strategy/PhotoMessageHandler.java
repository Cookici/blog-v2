package com.lrh.message.designpattern.strategy;

import com.lrh.common.util.IdUtil;
import com.lrh.message.config.designpattern.strategy.AbstractExecuteStrategy;
import com.lrh.message.enums.MessageTypeEnum;
import com.lrh.message.model.MessageModel;
import com.lrh.message.mq.producer.MessageProducer;
import com.lrh.message.netty.ChannelContext;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageHandler;
import com.lrh.message.netty.message.MessageVO;
import com.lrh.message.service.impl.ThreadPoolService;
import com.lrh.message.utils.MessageUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.designpattern.strategy
 * @ClassName: PhotoMessageHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/12 19:01
 */

@Slf4j
@Service
public class PhotoMessageHandler extends AbstractMessageHandler implements AbstractExecuteStrategy<MessageHandler, Void> {

    private final MessageProducer messageProducer;

    private final ThreadPoolService threadPoolService;

    private final RedisTemplate<String, Object> redisTemplate;

    public PhotoMessageHandler(MessageProducer messageProducer, ThreadPoolService threadPoolService, RedisTemplate<String, Object> redisTemplate) {
        this.messageProducer = messageProducer;
        this.threadPoolService = threadPoolService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void processMessage(MessageHandler messageHandler) {
        MessageDTO messageDTO = messageHandler.getMessageDTO();
        Channel channel = ChannelContext.getChannel(messageDTO.getToUserId());
        if (channel == null) {
            log.info("[WebSocketServer] 用户: {} 不在线", messageDTO.getToUserId());
            threadPoolService.setNoOnlineMessageCache(MessageUtil.convertMessageDTOToMessageModel(messageDTO));
            return;
        }
        MessageModel messageModel = MessageUtil.convertMessageDTOToMessageModel(messageDTO);
        try {
            MessageVO message = MessageUtil.convertMessageDTOToMessageVO(messageDTO);
            setCache(messageModel);
            channel.writeAndFlush(MessageUtil.getMessageToWebSocketFrame(channel, message));
        } catch (RuntimeException e) {
            log.error("[PhotoMessageHandler] processMessage error: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        threadPoolService.submitTask(() -> {
            messageProducer.syncSendMessage(messageModel);
        });
    }


    @Override
    public String mark() {
        return MessageTypeEnum.PhotoMessage.getMessageType();
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
     * @param messageModel 消息模型
     */
    @Override
    protected void setCache(MessageModel messageModel) {
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
        log.info("[PhotoMessageHandler] setRedis {},redis缓存成功", messageModel);
    }


}