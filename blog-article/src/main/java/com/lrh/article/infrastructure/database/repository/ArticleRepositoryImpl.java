package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.infrastructure.database.mapper.ArticleMapper;
import com.lrh.article.infrastructure.po.ArticlePO;
import com.lrh.common.constant.BusinessConstant;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<ArticlePO> getArticlesPage(ArticlePageQuery query, Long offset, Long limit) {
        return articleMapper.selectPageArticle(query, offset, limit);
    }

    @Override
    public Long countArticlesPage(ArticlePageQuery query) {
        return articleMapper.selectCountPage(query);
    }

    @Override
    public ArticlePO getArticlesById(String articleId) {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .eq(ArticlePO::getArticleId, articleId)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return articleMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer deleteArticleById(String articleId) {
        LambdaUpdateWrapper<ArticlePO> updateWrapper = Wrappers.lambdaUpdate(ArticlePO.class)
                .eq(ArticlePO::getArticleId, articleId)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(ArticlePO::getIsDeleted, BusinessConstant.IS_DELETED);
        return articleMapper.update(updateWrapper);
    }

    @Override
    public void updateArticleById(String articleId, String articleTitle, String articleContent) {
        if(articleTitle == null && articleContent == null){
            return;
        }
        LambdaUpdateWrapper<ArticlePO> updateWrapper = Wrappers.lambdaUpdate(ArticlePO.class)
                .eq(ArticlePO::getArticleId, articleId)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        if (articleTitle != null) {
            updateWrapper.set(ArticlePO::getArticleTitle, articleTitle);
        }
        if (articleContent != null) {
            updateWrapper.set(ArticlePO::getArticleContent, articleContent);
        }
        articleMapper.update(updateWrapper);
    }

    @Override
    public void insertArticle(ArticlePO articlePO) {
        articleMapper.insert(articlePO);
    }
}
