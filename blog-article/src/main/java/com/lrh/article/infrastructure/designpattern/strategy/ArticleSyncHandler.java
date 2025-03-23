package com.lrh.article.infrastructure.designpattern.strategy;

import com.lrh.article.domain.vo.ArticleMessageVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ArticleSyncHandler {

    public abstract void syncArticle(ArticleMessageVO articleId);
}

