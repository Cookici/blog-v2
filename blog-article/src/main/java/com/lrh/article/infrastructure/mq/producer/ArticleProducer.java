package com.lrh.article.infrastructure.mq.producer;

import com.lrh.article.domain.vo.ArticleMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class ArticleProducer {

    @Value("${rocketmq.producer.topic}")
    private String topic;

    private final RocketMQTemplate rocketMQTemplate;


    private final ThreadPoolExecutor threadPoolExecutor;


    public ArticleProducer(RocketMQTemplate rocketMQTemplate, ThreadPoolExecutor threadPoolExecutor) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.threadPoolExecutor = threadPoolExecutor;
    }


    public void sendMessage(ArticleMessageVO messageVO) {
        try {
            threadPoolExecutor.submit(() -> rocketMQTemplate.syncSend(topic, messageVO));
            log.info("[ArticleMessageVO] 成功发送消息: {} ", messageVO.getArticleId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
