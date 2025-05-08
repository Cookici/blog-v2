package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.domain.repository.ArticleLabelOperateRepository;
import com.lrh.article.infrastructure.database.convertor.ArticleLabelConvertor;
import com.lrh.article.infrastructure.database.mapper.ArticleLabelMapper;
import com.lrh.article.infrastructure.database.mapper.ArticleLikeMapper;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import com.lrh.article.infrastructure.po.ArticleLikePO;
import com.lrh.common.constant.BusinessConstant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final ArticleLabelMapper articleLabelMapper;
    private final ArticleLikeMapper articleLikeMapper;

    public ArticleLabelRepositoryImpl(ArticleLabelMapper articleLabelMapper, ArticleLikeMapper articleLikeMapper) {
        this.articleLabelMapper = articleLabelMapper;
        this.articleLikeMapper = articleLikeMapper;
    }


    @Override
    public List<ArticleLabelPO> getArticleLabelListByArticles(List<String> articleIdList) {
        List<ArticleLabelPO> articleLabelPOList = articleLabelMapper.getArticleLabelListByArticles(articleIdList);
        return articleLabelPOList;
    }

    @Override
    public List<String> getUserLikedLabels(String userId) {
        // 1. 获取用户点赞的文章ID列表
        LambdaQueryWrapper<ArticleLikePO> likeQueryWrapper = Wrappers.lambdaQuery(ArticleLikePO.class)
                .eq(ArticleLikePO::getUserId, userId)
                .eq(ArticleLikePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        List<ArticleLikePO> articleLikePOList = articleLikeMapper.selectList(likeQueryWrapper);

        if (articleLikePOList == null || articleLikePOList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 提取用户点赞的文章ID
        List<String> likedArticleIds = articleLikePOList.stream()
                .map(ArticleLikePO::getArticleId)
                .collect(Collectors.toList());

        // 3. 获取这些文章关联的标签
        List<ArticleLabelPO> articleLabelPOList = articleLabelMapper.getArticleLabelListByArticles(likedArticleIds);

        if (articleLabelPOList == null || articleLabelPOList.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 统计标签出现频率，选择用户最常点赞的标签
        Map<String, Long> labelFrequency = articleLabelPOList.stream()
                .collect(Collectors.groupingBy(ArticleLabelPO::getLabelId, Collectors.counting()));

        // 5. 按频率排序并返回前10个标签
        return labelFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleLabelPO> getIncludeDeleteArticleLabelListByArticles(List<String> articleIdList) {
        List<ArticleLabelPO> articleLabelPOList = articleLabelMapper.getIncludeDeleteArticleLabelListByArticles(articleIdList);
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
