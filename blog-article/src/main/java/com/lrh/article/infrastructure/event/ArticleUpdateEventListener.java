package com.lrh.article.infrastructure.event;

import com.lrh.article.domain.vo.ArticleMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class ArticleUpdateEventListener {
    @Value("${rocketmq.producer.topic}")
    private String topic;

    private final RocketMQTemplate rocketMQTemplate;


    private final ThreadPoolExecutor threadPoolExecutor;

    public ArticleUpdateEventListener(RocketMQTemplate rocketMQTemplate, ThreadPoolExecutor threadPoolExecutor) {
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

    @EventListener
    public void articleUpdateEvent(ArticleUpdateEvent event) {
        ArticleMessageVO articleMessageVO = new ArticleMessageVO();
        articleMessageVO.setArticleId(event.getArticleId());
        articleMessageVO.setStatus(event.getStatus());
        articleMessageVO.setUserName(event.getUserName());
        sendMessage(articleMessageVO);
    }
}
