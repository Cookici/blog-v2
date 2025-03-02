package com.lrh.article.infrastructure.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Configuration
public class RocketMQArticleConfig {
    @Value("${rocketmq.name-server}")
    String nameServerAddress;

    @Value("${rocketmq.consumer.group}")
    String consumerGroup;

    @Value("${rocketmq.consumer.topic}")
    String remoteTopic;
}
