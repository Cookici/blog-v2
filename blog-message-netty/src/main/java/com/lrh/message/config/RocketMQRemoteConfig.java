package com.lrh.message.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.config
 * @ClassName: RocketMQConfig
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/14 13:18
 */
@Slf4j
@Getter
@Configuration
public class RocketMQRemoteConfig {

    @Value("${rocketmq.name-server}")
    String nameServerAddress;

    @Value("${rocketmq.remote.consumer.group}")
    String consumerGroup;

    @Value("${rocketmq.remote.consumer.topic}")
    String remoteTopic;

    @Value("${netty.port}")
    Integer nettyPort;



}
