package com.lrh.article.infrastructure.mq.consumer;

import com.lrh.article.domain.vo.ArticleMessageVO;
import com.lrh.article.infrastructure.config.designpattern.strategy.AbstractStrategyChoose;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.consumer.group}")
public class ArticleConsumer implements RocketMQListener<ArticleMessageVO> {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final AbstractStrategyChoose abstractStrategyChoose;


    public ArticleConsumer(ThreadPoolExecutor threadPoolExecutor, AbstractStrategyChoose abstractStrategyChoose) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.abstractStrategyChoose = abstractStrategyChoose;
    }

    @Override
    public void onMessage(ArticleMessageVO messageVO) {
        log.info("[ArticleMessageVO] 消费消息: {}", messageVO.getArticleId());
        if (messageVO.getArticleId() == null || messageVO.getArticleId().length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException("校验失败");
        }
        threadPoolExecutor.submit(() -> {
            abstractStrategyChoose.chooseAndExecute(messageVO.getStatus().getStatus(), messageVO);
            log.info("[ArticleMessageVO] 成功消费消息: {}", messageVO.getArticleId());
        });
    }
}
