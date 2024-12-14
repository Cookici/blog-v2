package com.lrh.article.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.infrastructure.mapper.ArticleMapper;
import com.lrh.article.infrastructure.po.ArticlePO;
import org.springframework.stereotype.Repository;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.repository
 * @ClassName: ArticleRepositoryImpl
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:54
 */

@Repository
public class ArticleRepositoryImpl implements ArticleOperateRepository {

    private final ArticleMapper articleMapper;

    public ArticleRepositoryImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }


    @Override
    public Page<ArticlePO> getArticlesPage(ArticlePageQuery query) {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .and(wrapper -> wrapper
                        .like(ArticlePO::getArticleTitle, query.getArticleTitle())
                        .or()
                        .like(ArticlePO::getArticleContent, query.getArticleContent())
                ).orderByDesc(ArticlePO::getUpdateTime);
        Page<ArticlePO> page =
                new Page<>(query.getPageQuery().getPage(), query.getPageQuery().getPageSize());
        return articleMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Long countArticlesPage(ArticlePageQuery query) {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .and(wrapper -> wrapper
                        .like(ArticlePO::getArticleTitle, query.getArticleTitle())
                        .or()
                        .like(ArticlePO::getArticleContent, query.getArticleContent())
                );
        return articleMapper.selectCount(queryWrapper);
    }
}
