package com.lrh.article.infrastructure.event;

import com.lrh.article.domain.vo.ArticleStatusEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Getter
public class ArticleUpdateEvent extends ApplicationEvent {
    private String articleId;
    private String userName;
    private ArticleStatusEnum status;

    public ArticleUpdateEvent(Object source, String articleId, String userName, ArticleStatusEnum status) {
        super(source);
        this.articleId = articleId;
        this.userName = userName;
        this.status = status;

    }
}
