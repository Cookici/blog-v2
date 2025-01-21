package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.domain.repository.ArticleLabelOperateRepository;
import com.lrh.article.infrastructure.database.convertor.ArticleLabelConvertor;
import com.lrh.article.infrastructure.database.mapper.ArticleLabelMapper;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import com.lrh.common.constant.BusinessConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.database.repository
 * @ClassName: ArticleLabelRepositoryImpl
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 12:44
 */
@Repository
public class ArticleLabelRepositoryImpl implements ArticleLabelOperateRepository {

    @Autowired
    private ArticleLabelMapper articleLabelMapper;

    @Override
    public List<ArticleLabelPO> getArticleLabelListByArticles(List<String> articleIdList) {
        List<ArticleLabelPO> articleLabelPOList = articleLabelMapper.getArticleLabelListByArticles(articleIdList);
        return articleLabelPOList;
    }

    @Override
    public void upsertLabelForArticle(String articleId, List<String> labelIdList) {
        List<ArticleLabelPO> articleLabelPOList =
                ArticleLabelConvertor.buildArticleLabelPOConvertor(articleId, labelIdList);
        articleLabelMapper.batchUpsert(articleLabelPOList);
    }

    @Override
    public void deleteLabelForArticle(String articleId) {
        LambdaUpdateWrapper<ArticleLabelPO> updateWrapper = Wrappers.lambdaUpdate(ArticleLabelPO.class)
                .eq(ArticleLabelPO::getArticleId, articleId)
                .eq(ArticleLabelPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(ArticleLabelPO::getIsDeleted, BusinessConstant.IS_DELETED);
        articleLabelMapper.update(updateWrapper);
    }

    @Override
    public void restoreDeletedArticleLabel(String articleId, List<String> labelIdList) {
        articleLabelMapper.restoreDeleted(articleId, labelIdList);
    }

}
