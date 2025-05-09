package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.domain.repository.ArticleLikeRepository;
import com.lrh.article.infrastructure.database.mapper.ArticleLikeMapper;
import com.lrh.article.infrastructure.po.ArticleLikePO;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.util.IdUtil;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.infrastructure.database.repository
 * @ClassName: ArticleLikeRepositoryImpl
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/26 20:42
 */
@Repository
public class ArticleLikeRepositoryImpl implements ArticleLikeRepository {

    private final ArticleLikeMapper articleLikeMapper;

    public ArticleLikeRepositoryImpl(ArticleLikeMapper articleLikeMapper) {
        this.articleLikeMapper = articleLikeMapper;
    }

    @Override
    public void incrArticleLikeCount(String articleId, String userId) {
        ArticleLikePO articleLikePO = new ArticleLikePO();
        articleLikePO.setRecordId("article_like_"+ IdUtil.getUuid());
        articleLikePO.setArticleId(articleId);
        articleLikePO.setUserId(userId);
        articleLikeMapper.insert(articleLikePO);
    }

    @Override
    public void deleteArticleLike(String articleId, String userId) {
        LambdaUpdateWrapper<ArticleLikePO> updateWrapper = Wrappers.lambdaUpdate(ArticleLikePO.class)
                .eq(ArticleLikePO::getUserId, userId)
                .eq(ArticleLikePO::getArticleId, articleId)
                .eq(ArticleLikePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(ArticleLikePO::getIsDeleted, BusinessConstant.IS_DELETED);
        articleLikeMapper.update(updateWrapper);
    }

    @Override
    public Set<String> getLikedArticleIdsByUserId(String userId) {
        LambdaQueryWrapper<ArticleLikePO> queryWrapper = Wrappers.lambdaQuery(ArticleLikePO.class)
                .eq(ArticleLikePO::getUserId, userId)
                .eq(ArticleLikePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        List<ArticleLikePO> articleLikePOList = articleLikeMapper.selectList(queryWrapper);
        Set<String> likedArticleIds = new HashSet<>();
        for (ArticleLikePO articleLikePO : articleLikePOList) {
            likedArticleIds.add(articleLikePO.getArticleId());
        }
        return likedArticleIds;
    }

    @Override
    public Map<String, Set<String>> getUserLikedArticlesMap() {
        LambdaQueryWrapper<ArticleLikePO> queryWrapper = Wrappers.lambdaQuery(ArticleLikePO.class)
                .eq(ArticleLikePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        List<ArticleLikePO> articleLikePOList = articleLikeMapper.selectList(queryWrapper);
        Map<String, Set<String>> userLikedArticlesMap = new HashMap<>();
        for (ArticleLikePO articleLikePO : articleLikePOList) {
            String userId = articleLikePO.getUserId();
            String articleId = articleLikePO.getArticleId();
            userLikedArticlesMap.computeIfAbsent(userId, k -> new HashSet<>()).add(articleId);
        }
        return userLikedArticlesMap;
    }

    @Override
    public void deleteLikeByArticleId(String articleId) {
        LambdaUpdateWrapper<ArticleLikePO> updateWrapper = Wrappers.lambdaUpdate(ArticleLikePO.class)
                .eq(ArticleLikePO::getArticleId, articleId)
                .eq(ArticleLikePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(ArticleLikePO::getIsDeleted, BusinessConstant.IS_DELETED);
        articleLikeMapper.update(updateWrapper);
    }

    @Override
    public List<ArticleLikePO> getDeletedLikeListByArticleId(String articleId) {
        LambdaQueryWrapper<ArticleLikePO> queryWrapper = Wrappers.lambdaQuery(ArticleLikePO.class)
                .eq(ArticleLikePO::getArticleId, articleId)
                .eq(ArticleLikePO::getIsDeleted, BusinessConstant.IS_DELETED);
        return articleLikeMapper.selectList(queryWrapper);
    }



}
