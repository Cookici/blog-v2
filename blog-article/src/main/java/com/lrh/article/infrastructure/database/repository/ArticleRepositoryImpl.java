package com.lrh.article.infrastructure.database.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lrh.article.application.cqe.article.*;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.domain.vo.ArticleStatusEnum;
import com.lrh.article.infrastructure.database.esDao.ArticleEsDao;
import com.lrh.article.infrastructure.database.mapper.ArticleLabelMapper;
import com.lrh.article.infrastructure.database.mapper.ArticleMapper;
import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import com.lrh.article.infrastructure.po.ArticlePO;
import com.lrh.common.constant.BusinessConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.repository
 * @ClassName: ArticleRepositoryImpl
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:54
 */

@Slf4j
@Repository
public class ArticleRepositoryImpl implements ArticleOperateRepository {

    private final ArticleMapper articleMapper;
    private final ArticleEsDao articleEsDao;
    private final ArticleLabelMapper articleLabelMapper;

    public ArticleRepositoryImpl(ArticleMapper articleMapper, ArticleEsDao articleEsDao, ArticleLabelMapper articleLabelMapper) {
        this.articleMapper = articleMapper;
        this.articleEsDao = articleEsDao;
        this.articleLabelMapper = articleLabelMapper;
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
    public Integer updateArticleSatusById(String articleId, String status) {
        LambdaUpdateWrapper<ArticlePO> updateWrapper = Wrappers.lambdaUpdate(ArticlePO.class)
                .eq(ArticlePO::getArticleId, articleId)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(ArticlePO::getStatus, status);

        return articleMapper.update(updateWrapper);
    }

    @Override
    public void updateArticleById(String articleId, String articleTitle, String articleContent, String status) {
        if (articleTitle == null && articleContent == null) {
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
        if (status != null) {
            updateWrapper.set(ArticlePO::getStatus, status);
        }
        articleMapper.update(updateWrapper);
    }


    @Override
    public void insertArticle(ArticlePO articlePO) {
        articleMapper.insert(articlePO);
    }

    @Override
    public List<ArticlePO> getArticlesByUserId(String userId) {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .eq(ArticlePO::getUserId, userId)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return articleMapper.selectList(queryWrapper);
    }

    @Override
    public Long countUserArticlesPage(ArticleUserPageQuery query) {
        return articleMapper.selectUserCountPage(query);
    }

    @Override
    public List<ArticlePO> getUserArticlesPage(ArticleUserPageQuery query, Long offset, Long limit) {
        return articleMapper.selectUserPageArticle(query, offset, limit);
    }

    @Override
    public List<ArticlePO> getArticleByIds(List<String> articleIdList) {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .in(ArticlePO::getArticleId, articleIdList)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return articleMapper.selectList(queryWrapper);
    }

    @Override
    public List<String> getAllArticleIds() {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .select(ArticlePO::getArticleId)
                .eq(ArticlePO::getStatus, ArticleStatusEnum.Published)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return articleMapper.selectList(queryWrapper)
                .stream()
                .map(ArticlePO::getArticleId)
                .collect(Collectors.toList());
    }

    @Override
    public void batchUpdateArticleMetrics(List<Map<String, Object>> updateBatch) {
        if (updateBatch.isEmpty()) {
            return;
        }
        articleMapper.batchUpdateMetrics(updateBatch);
    }

    @Override
    public void updateArticleEsMetrics(String articleId, Long likeCount, Long viewCount) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("likeCount", likeCount);
        updateMap.put("viewCount", viewCount);
        articleEsDao.updateArticleMetrics(articleId, updateMap);
    }

    @Override
    public Long countUserArticlesEsPage(ArticleEsUserPageQuery query) {
        return articleEsDao.countUserArticlesEsPage(query.getUserId(), query.getElement());
    }

    @Override
    public List<ArticleDO> getUserArticlesEsPage(ArticleEsUserPageQuery query) {
        return articleEsDao.getUserArticlesEsPage(query.getLimit(), query.getOffset(), query.getUserId(), query.getElement());
    }

    @Override
    public List<ArticleDO> getHotArticles(List<String> excludeArticleIds) {
        return articleEsDao.getHotArticles(excludeArticleIds);
    }

    @Override
    public List<ArticleDO> getHotArticlesTop(Integer top) {
        return articleEsDao.getHotArticlesTop(top);
    }

    @Override
    public List<ArticleDO> getArticleListByEsQuery(ArticleListQuery query) {
        return articleEsDao.getArticleList(query.getOffset(), query.getLimit(), query.getElement());
    }


    @Override
    public Long countArticlesByEsQuery(ArticleListQuery query) {
        return articleEsDao.countArticle(query.getElement());
    }


    @Override
    public void deleteEsById(String articleId) {
        articleEsDao.deleteArticleById(articleId);
    }

    @Override
    public void saveArticleDo(ArticleDO articleDO) {
        articleEsDao.saveArticleDo(articleDO);
    }

    @Override
    public Long countLikeArticle(ArticleLikePageQuery query, Set<String> likeIds) {
        return articleEsDao.countLikeArticleList(query.getElement(), likeIds);
    }

    @Override
    public List<ArticleDO> getLikeArticleList(ArticleLikePageQuery query, Set<String> likeArticleIds) {
        return articleEsDao.getLikeArticleList(query.getOffset(), query.getLimit(), query.getElement(), likeArticleIds);
    }

    @Override
    public List<ArticlePO> getRecommendArticles(String userId, List<String> labelNames) {
        // 如果没有标签偏好，直接返回热门文章
        if (labelNames == null || labelNames.isEmpty()) {
            return getHotArticles(10);
        }

        // 1. 获取与用户标签偏好相关的文章标签关系
        LambdaQueryWrapper<ArticleLabelPO> labelQueryWrapper = Wrappers.lambdaQuery(ArticleLabelPO.class)
                .in(ArticleLabelPO::getLabelId, labelNames)
                .eq(ArticleLabelPO::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        List<ArticleLabelPO> articleLabelPOList = articleLabelMapper.selectList(labelQueryWrapper);

        if (articleLabelPOList == null || articleLabelPOList.isEmpty()) {
            return getHotArticles(10);
        }

        // 2. 提取相关文章ID
        List<String> recommendArticleIds = articleLabelPOList.stream()
                .map(ArticleLabelPO::getArticleId)
                .distinct()
                .collect(Collectors.toList());

        // 3. 查询文章详情，排除用户自己的文章和已删除的文章
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .in(ArticlePO::getArticleId, recommendArticleIds)
                .ne(ArticlePO::getUserId, userId)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .eq(ArticlePO::getStatus, ArticleStatusEnum.Published);

        // 4. 按更新时间降序排序，限制返回10条
        queryWrapper.orderByDesc(ArticlePO::getUpdateTime);

        // 5. 查询并返回推荐文章列表
        List<ArticlePO> recommendArticles = articleMapper.selectList(queryWrapper);

        // 如果推荐文章不足10条，补充热门文章
        if (recommendArticles.size() < 10) {
            // 获取已推荐的文章ID
            Set<String> existingArticleIds = recommendArticles.stream()
                    .map(ArticlePO::getArticleId)
                    .collect(Collectors.toSet());

            // 查询热门文章，排除已推荐的文章
            LambdaQueryWrapper<ArticlePO> hotQueryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                    .notIn(!existingArticleIds.isEmpty(), ArticlePO::getArticleId, existingArticleIds)
                    .ne(ArticlePO::getUserId, userId)
                    .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                    .eq(ArticlePO::getStatus, ArticleStatusEnum.Published)
                    .orderByDesc(ArticlePO::getViewCount)
                    .last("LIMIT " + (10 - recommendArticles.size()));

            List<ArticlePO> hotArticles = articleMapper.selectList(hotQueryWrapper);
            recommendArticles.addAll(hotArticles);
        }

        return recommendArticles;
    }

    @Override
    public List<ArticlePO> getHotArticles(int limit) {
        // 查询热门文章，按浏览量和点赞量降序排序
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class)
                .eq(ArticlePO::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .eq(ArticlePO::getStatus, ArticleStatusEnum.Published)
                .orderByDesc(ArticlePO::getViewCount)
                .last("LIMIT " + limit);

        return articleMapper.selectList(queryWrapper);
    }

    @Override
    public Long countArticlesForReconciliation(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class);


        if (startTime != null && endTime != null) {
            queryWrapper.between(ArticlePO::getUpdateTime, startTime, endTime);
        }

        return articleMapper.selectCount(queryWrapper);
    }

    @Override
    public List<String> getArticleIdsForReconciliation(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        LambdaQueryWrapper<ArticlePO> queryWrapper = Wrappers.lambdaQuery(ArticlePO.class);

        queryWrapper.select(ArticlePO::getArticleId);

        // 如果指定了时间范围，则添加时间条件
        if (startTime != null && endTime != null) {
            queryWrapper.between(ArticlePO::getUpdateTime, startTime, endTime);
        }

        int offset = (page - 1) * size;

        queryWrapper.orderByDesc(ArticlePO::getUpdateTime).last("LIMIT " + offset + " , " + size);


        return articleMapper.selectList(queryWrapper).stream().map(ArticlePO::getArticleId).collect(Collectors.toList());
    }

    @Override
    public List<ArticleDO> getArticleEsByIds(List<String> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<ArticleDO> result = new ArrayList<>();
        for (String articleId : articleIds) {
            ArticleDO article = articleEsDao.getArticleById(articleId);
            if (article != null) {
                result.add(article);
            }
        }
        return result;
    }

    @Override
    public int batchSaveArticleDo(List<ArticleDO> articlesToAdd) {
        if (articlesToAdd == null || articlesToAdd.isEmpty()) {
            return 0;
        }
        return articleEsDao.batchSaveArticles(articlesToAdd);
    }

    @Override
    public int batchUpdateArticleDo(List<ArticleDO> needUpdateEsArticles) {
        if (needUpdateEsArticles == null || needUpdateEsArticles.isEmpty()) {
            return 0;
        }
        return articleEsDao.batchUpdateArticles(needUpdateEsArticles);
    }

    @Override
    public Integer updateArticleEsUserName(String userId, String userName) {
        if (userId == null || userId.isEmpty() || userName == null || userName.isEmpty()) {
            log.warn("[ArticleRepositoryImpl] updateArticleEsUserName 更新用户文章用户名参数无效: userId={}, userName={}", userId, userName);
            return 0;
        }
        
        log.info("[ArticleRepositoryImpl] updateArticleEsUserName 开始更新用户[{}]的文章用户名为[{}]", userId, userName);
        
        // 直接调用ES DAO的方法批量更新用户名
        int updatedCount = articleEsDao.updateUserNameByUserId(userId, userName);
        log.info("[ArticleRepositoryImpl] updateArticleEsUserName ES中成功更新用户[{}]的{}篇文章用户名", userId, updatedCount);
        return updatedCount;
    }
}
