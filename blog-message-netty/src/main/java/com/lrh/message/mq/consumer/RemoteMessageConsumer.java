package com.lrh.message.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.lrh.message.config.RocketMQRemoteConfig;
import com.lrh.message.config.designpattern.strategy.AbstractStrategyChoose;
import com.lrh.message.constants.MessageConstant;
import com.lrh.message.netty.ChannelContext;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageHandler;
import com.lrh.message.netty.message.MessageVO;
import com.lrh.message.utils.MessageUtil;
import com.lrh.message.utils.NettyUtil;
import com.lrh.message.utils.RedisKeyUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.mq.consumer
 * @ClassName: RemoteMessageConsumer
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/14 01:37
 */
@Slf4j
@Component
public class RemoteMessageConsumer implements CommandLineRunner {

    private final RocketMQRemoteConfig rocketMQConfig;

    private final AbstractStrategyChoose abstractStrategyChoose;

    private final RedisTemplate<String, Object> redisTemplate;

    public RemoteMessageConsumer(RocketMQRemoteConfig rocketMQConfig, AbstractStrategyChoose abstractStrategyChoose, RedisTemplate<String, Object> redisTemplate) {
        this.rocketMQConfig = rocketMQConfig;
        this.abstractStrategyChoose = abstractStrategyChoose;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Async
    public void run(String... args) throws Exception {
        String dynamicTopic = NettyUtil.getDynamicTopic(rocketMQConfig.getRemoteTopic(),
                String.valueOf(rocketMQConfig.getNettyPort()));

        String group = NettyUtil.getGroupOnlyOne(rocketMQConfig.getConsumerGroup(),
                String.valueOf(rocketMQConfig.getNettyPort()));
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(rocketMQConfig.getNameServerAddress());
        consumer.subscribe(dynamicTopic, "*");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setInstanceName(dynamicTopic);

        consumer.registerMessageListener((MessageListenerOrderly) (messageExtList, context) -> {
            for (MessageExt msg : messageExtList) {
                try {
                    MessageDTO messageDTO = JSON.parseObject(msg.getBody(), MessageDTO.class);
                    log.info("[RemoteMessageConsumer] 接收到消息: {}", messageDTO);
                    Channel channel = ChannelContext.getChannel(messageDTO.getToUserId());
                    if (channel == null) {
                        log.info("[RemoteMessageConsumer] 用户不在线");
                        MessageVO message = MessageUtil.convertMessageDTOToMessageVO(messageDTO, MessageConstant.STATUS_OFFLINE);
                        setCache(message);
                        continue;
                    }
                    abstractStrategyChoose.chooseAndExecute(messageDTO.getMessageType(),
                            new MessageHandler(messageDTO, channel));
                } catch (Exception e) {
                    log.info("[RemoteMessageConsumer] 消息处理失败", e);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }
            return ConsumeOrderlyStatus.SUCCESS;
        });

        consumer.start();
        log.info("[RemoteMessageConsumer] 顺序消费者启动, topic: {}", dynamicTopic);
    }

    /**
     * ARGV[1]: ZSet 分值 (timestamp)
     * ARGV[2]: ZSet 数据 (序列化的 MessageModel)
     * ARGV[3]: 过期时间 (秒)
     *
     * @param messageVO 消息前端展示
     */
    protected void setCache(MessageVO messageVO) {
        String redisKey = RedisKeyUtil.getMessageOneToOneRedisKey(messageVO.getUserId(), messageVO.getToUserId());
        String luaScript =
                "redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2]) " +
                        "redis.call('EXPIRE', KEYS[1], ARGV[3])";

        List<String> keys = Collections.singletonList(redisKey);
        List<Object> args = Arrays.asList(
                messageVO.getTimestamp(),
                messageVO,
                Duration.ofDays(7).getSeconds()
        );

        RedisScript<Void> redisScript = new DefaultRedisScript<>(luaScript, Void.class);
        redisTemplate.execute(redisScript, keys, args.toArray());
        log.info("[RemoteMessageConsumer] setCache {},redis缓存成功", messageVO);
    }

}

