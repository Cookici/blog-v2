package com.lrh.article.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.infrastructure.po.ArticlePO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.service
 * @ClassName: ArticleService
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:24
 */
@Service
public class ArticleOperateService {
    private final ArticleOperateRepository articleRepository;

    public ArticleOperateService(ArticleOperateRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Page<ArticleEntity> getArticlesPage(ArticlePageQuery articlePageQuery) {
        // 获取文章
        Page<ArticlePO> articlesPage = articleRepository.getArticlesPage(articlePageQuery);
        List<ArticlePO> articles = articlesPage.getRecords();
        List<ArticleEntity> articleEntityList = new ArrayList<>();
        articles.forEach(article -> {
            articleEntityList.add(ArticleEntity.fromPO(article));
        });

        Page<ArticleEntity> articleEntityPage = new Page<>(articlesPage.getCurrent(),
                articlesPage.getSize(), articlesPage.getTotal());
        articleEntityPage.setRecords(articleEntityList);
        return articleEntityPage;
    }

    /**
     * 计算文章的总数
     *
     * @return 总文章数
     */
    public Long countArticlesPage(ArticlePageQuery articlePageQuery) {
        return articleRepository.countArticlesPage(articlePageQuery);
    }
}
