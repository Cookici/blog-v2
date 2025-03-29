package com.lrh.article.infrastructure.designpattern.strategy;

import com.lrh.article.domain.service.ArticleOperateService;
import com.lrh.article.domain.vo.ArticleMessageVO;
import com.lrh.article.domain.vo.ArticleStatusEnum;
import com.lrh.article.infrastructure.config.designpattern.strategy.AbstractExecuteStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ArticleSyncSaveHandler extends ArticleSyncHandler implements AbstractExecuteStrategy<ArticleMessageVO, Void> {
    private final ArticleOperateService articleOperateService;

    public ArticleSyncSaveHandler(ArticleOperateService articleOperateService) {
        this.articleOperateService = articleOperateService;
    }

    @Override
    public void syncArticle(ArticleMessageVO article) {
        articleOperateService.syncSaveArticle(article);
    }

    @Override
    public String mark() {
        return ArticleStatusEnum.UnderAudit.getStatus();
    }


    @Override
    public void execute(ArticleMessageVO requestParam) {
        syncArticle(requestParam);
    }
}
