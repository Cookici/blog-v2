package com.lrh.article.infrastructure.designpattern.strategy;

import com.lrh.article.domain.service.ArticleOperateService;
import com.lrh.article.domain.vo.ArticleMessageVO;
import com.lrh.article.domain.vo.ArticleStatusEnum;
import com.lrh.article.infrastructure.config.designpattern.strategy.AbstractExecuteStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ArticleSyncDeleteHandler extends ArticleSyncUpdateHandler implements AbstractExecuteStrategy<ArticleMessageVO, Void> {
    private final ArticleOperateService articleOperateService;

    public ArticleSyncDeleteHandler(ArticleOperateService articleOperateService) {
        this.articleOperateService = articleOperateService;
    }

    @Override
    public void syncArticleUpdate(ArticleMessageVO article) {
        articleOperateService.deleteById(article.getArticleId());
    }

    @Override
    public String mark() {
        return ArticleStatusEnum.Deleted.getStatus();
    }


    @Override
    public void execute(ArticleMessageVO requestParam) {
        syncArticleUpdate(requestParam);
    }
}
