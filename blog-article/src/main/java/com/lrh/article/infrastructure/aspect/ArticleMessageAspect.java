package com.lrh.article.infrastructure.aspect;

import com.lrh.article.domain.vo.ArticleMessageVO;
import com.lrh.article.infrastructure.mq.producer.ArticleProducer;
import com.lrh.common.annotations.ArticleSyncRecords;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class ArticleMessageAspect {
    private final ArticleProducer articleProducer;

    public ArticleMessageAspect(ArticleProducer articleProducer) {
        this.articleProducer = articleProducer;
    }

    @AfterReturning(pointcut = "@annotation(articleSyncRecords)", returning = "result")
    public void afterSendMessage(Object result, ArticleSyncRecords articleSyncRecords) {
        if (result != null) {
            ArticleMessageVO articleMessageVO = (ArticleMessageVO) result;
            articleProducer.sendMessage(articleMessageVO);
        }
    }
}
