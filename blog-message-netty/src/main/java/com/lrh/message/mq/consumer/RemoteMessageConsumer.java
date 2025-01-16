package com.lrh.message.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.lrh.message.config.RocketMQRemoteConfig;
import com.lrh.message.config.designpattern.strategy.AbstractStrategyChoose;
import com.lrh.message.mq.producer.MessageProducer;
import com.lrh.message.netty.ChannelContext;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageHandler;
import com.lrh.message.service.impl.ThreadPoolService;
import com.lrh.message.utils.MessageUtil;
import com.lrh.message.utils.NettyUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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

    private final ThreadPoolService threadPoolService;

    private final MessageProducer messageProducer;

    public RemoteMessageConsumer(RocketMQRemoteConfig rocketMQConfig, AbstractStrategyChoose abstractStrategyChoose,
                                 ThreadPoolService threadPoolService, MessageProducer messageProducer) {
        this.rocketMQConfig = rocketMQConfig;
        this.abstractStrategyChoose = abstractStrategyChoose;
        this.threadPoolService = threadPoolService;
        this.messageProducer = messageProducer;
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

        consumer.registerMessageListener((MessageListenerConcurrently) (messageExtList, context) -> {
            for (MessageExt msg : messageExtList) {
                MessageDTO messageDTO = JSON.parseObject(msg.getBody(), MessageDTO.class);
                log.info("[RemoteMessageConsumer] 接收到消息: {}", messageDTO);
                Channel channel = ChannelContext.getChannel(messageDTO.getToUserId());
                if (channel == null) {
                    log.info("[RemoteMessageConsumer] 用户不在线");
                    threadPoolService.setNoOnlineMessageCache(MessageUtil.convertMessageDTOToMessageModel(messageDTO));
                    continue;
                }
                try {
                    abstractStrategyChoose.chooseAndExecute(messageDTO.getMessageType(),
                            new MessageHandler(messageDTO, channel));
                } catch (Exception e) {
                    log.info("[RemoteMessageConsumer] 消息处理失败");
                    //TODO remoteErrorMessageHandler
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        log.info("[RemoteMessageConsumer] 消费者启动,topic: {}", dynamicTopic);
    }
}

