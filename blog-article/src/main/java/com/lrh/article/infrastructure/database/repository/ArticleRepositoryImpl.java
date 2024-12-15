package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.infrastructure.database.convertor.ArticleConvertor;
import com.lrh.article.infrastructure.database.mapper.ArticleMapper;
import com.lrh.article.infrastructure.po.ArticlePO;
import com.lrh.common.constant.BusinessConstant;
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
    public Page<ArticleEntity> getArticlesPage(ArticlePageQuery query) {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);

        if (query.getArticleTitle() != null && !query.getArticleTitle().isEmpty()) {
            queryWrapper.like(ArticlePO::getArticleTitle, query.getArticleTitle());
        }
        if (query.getArticleContent() != null && !query.getArticleContent().isEmpty()) {
            queryWrapper.like(ArticlePO::getArticleContent, query.getArticleContent());
        }

        queryWrapper.orderByAsc(ArticlePO::getUpdateTime);

        Page<ArticlePO> page = new Page<>(query.getPage(), query.getPageSize());
        return ArticleConvertor.toPageArticleEntityConvertor(articleMapper.selectPage(page, queryWrapper));
    }

    @Override
    public Long countArticlesPage(ArticlePageQuery query) {
        return articleMapper.selectCountPage(query);
    }
}
