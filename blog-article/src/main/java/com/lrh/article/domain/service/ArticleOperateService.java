package com.lrh.article.domain.service;

import com.lrh.article.application.cqe.article.*;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.entity.UserArticleDataEntity;
import com.lrh.article.domain.repository.*;
import com.lrh.article.infrastructure.database.convertor.ArticleConvertor;
import com.lrh.article.infrastructure.database.convertor.LabelConvertor;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import com.lrh.article.infrastructure.po.ArticlePO;
import com.lrh.article.infrastructure.po.LabelPO;
import com.lrh.article.util.LockUtil;
import com.lrh.common.context.UserContext;
import com.lrh.common.util.IdUtil;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private final ArticleLabelOperateRepository articleLabelOperateRepository;
    private final LabelOperateRepository labelOperateRepository;
    private final CommentOperateRepository commentOperateRepository;
    private final ArticleCacheRepository articleCacheRepository;
    private final RedissonClient redissonClient;

    public ArticleOperateService(ArticleOperateRepository articleRepository, ArticleLabelOperateRepository articleLabelOperateRepository,
                                 LabelOperateRepository labelOperateRepository, CommentOperateRepository commentOperateRepository, ArticleCacheRepository articleCacheRepository, RedissonClient redissonClient) {
        this.articleRepository = articleRepository;
        this.articleLabelOperateRepository = articleLabelOperateRepository;
        this.labelOperateRepository = labelOperateRepository;
        this.commentOperateRepository = commentOperateRepository;
        this.articleCacheRepository = articleCacheRepository;
        this.redissonClient = redissonClient;
    }


    public List<ArticleEntity> getArticlesPage(ArticlePageQuery articlePageQuery) {
        List<ArticlePO> articlePOList = articleRepository.getArticlesPage(articlePageQuery,
                articlePageQuery.getOffset(), articlePageQuery.getLimit());
        if (articlePOList == null) {
            return new ArrayList<>();
        }

        List<ArticleEntity> articleEntityList = ArticleConvertor.toArticleEntityListConvertor(articlePOList);

        // 为每篇文章设置对应的标签列表
        setLabelListForArticleEntityList(articleEntityList);

        // 返回分页结果
        return articleEntityList;
    }

    private void setLabelListForArticleEntityList(List<ArticleEntity> articleEntityList) {
        // 提取文章 ID 列表
        List<String> articleIdList = articleEntityList.stream()
                .map(ArticleEntity::getArticleId)
                .collect(Collectors.toList());

        // 获取文章与标签 ID 的映射关系
        List<ArticleLabelPO> articleLabelPOList = articleLabelOperateRepository.getArticleLabelListByArticles(articleIdList);
        Map<String, List<String>> articleIdToLabelIdsMap = articleLabelPOList.stream().collect(Collectors.groupingBy(
                ArticleLabelPO::getArticleId,
                Collectors.mapping(ArticleLabelPO::getLabelId, Collectors.toList())
        ));

        // 获取标签详细信息
        List<LabelPO> labelPOList = labelOperateRepository.getLabelListByIds(
                articleIdToLabelIdsMap.values().stream()
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList())
        );

        List<LabelEntity> labelEntityList = LabelConvertor.toListLabelEntityConvertor(labelPOList);

        // 构建标签 ID 到标签实体的映射
        Map<String, LabelEntity> labelIdToLabelEntityMap = labelEntityList.stream()
                .collect(Collectors.toMap(LabelEntity::getLabelId, label -> label));

        // 为每篇文章设置对应的标签列表
        articleEntityList.forEach(articleEntity -> {
            List<String> labelIds = articleIdToLabelIdsMap.getOrDefault(articleEntity.getArticleId(), Collections.emptyList());
            List<LabelEntity> labels = labelIds.stream()
                    .map(labelIdToLabelEntityMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            articleEntity.setLabelEntityList(labels);
        });
    }

    /**
     * 计算文章的总数
     *
     * @return 总文章数
     */
    public Long countArticlesPage(ArticlePageQuery articlePageQuery) {
        return articleRepository.countArticlesPage(articlePageQuery);
    }

    public ArticleEntity getArticleById(ArticleQuery articleQuery) {
        ArticlePO articlePO = articleRepository.getArticlesById(articleQuery.getArticleId());
        if (articlePO == null) {
            return null;
        }
        ArticleEntity articleEntity = ArticleEntity.fromPO(articlePO);
        List<LabelPO> articleIdList = labelOperateRepository.selectLabelsByArticleId(articleQuery.getArticleId());
        if (articleIdList == null) {
            articleEntity.setLabelEntityList(new ArrayList<>());
        } else {
            articleEntity.setLabelEntityList(LabelConvertor.toListLabelEntityConvertor(articleIdList));
        }
        return articleEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteArticleById(ArticleDeleteCommand command) {
        validExceptionOperate(command.getArticleId(), command.getUserId());
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.tryWriteLock(String.format(RedisConstant.ARTICLE_LOCK, command.getArticleId()), () -> {
            articleLabelOperateRepository.deleteLabelForArticle(command.getArticleId());
            Integer update = articleRepository.deleteArticleById(command.getArticleId());
            if (update == null || update == 0) {
                return;
            }
            commentOperateRepository.deleteCommentsByArticle(command.getArticleId());
            articleCacheRepository.deleteArticleCache(command.getArticleId());
        });
    }

    private void validExceptionOperate(String articleId, String userId) {
        ArticlePO articlePO = articleRepository.getArticlesById(articleId);
        if (articlePO == null || !Objects.equals(articlePO.getUserId(), userId)) {
            throw new RuntimeException("非法操作");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateArticleById(ArticleUpdateCommand command) {
        validExceptionOperate(command.getArticleId(), command.getUserId());
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.tryWriteLock(String.format(RedisConstant.ARTICLE_LOCK, command.getArticleId()), () -> {
            articleRepository.updateArticleById(command.getArticleId(), command.getArticleTitle(), command.getArticleContent());
            if (command.getLabelIdList().isEmpty()) {
                return;
            }
            articleLabelOperateRepository.deleteLabelForArticle(command.getArticleId());
            articleLabelOperateRepository.restoreDeletedArticleLabel(command.getArticleId(), command.getLabelIdList());
            articleLabelOperateRepository.upsertLabelForArticle(command.getArticleId(), command.getLabelIdList());
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertArticle(ArticleInsertCommand command) {
        ArticlePO articlePO = ArticlePO.builder()
                .articleId("article_" + IdUtil.getUuid())
                .articleTitle(command.getArticleTitle())
                .articleContent(command.getArticleContent())
                .userId(command.getUserId())
                .build();
        articleRepository.insertArticle(articlePO);
        if (command.getLabelIdList().isEmpty()) {
            return;
        }
        articleLabelOperateRepository.upsertLabelForArticle(articlePO.getArticleId(), command.getLabelIdList());
    }

    public void articleViewIncrement(ArticleViewCommand command) {
        articleCacheRepository.incrArticleViewCount(command.getArticleId(), UserContext.getUserId());
    }

    public void articleLikeIncrement(ArticleLikeCommand command) {
        articleCacheRepository.incrArticleLikeCount(command.getArticleId(), UserContext.getUserId());
    }

    public void articleNoLoginViewIncrement(ArticleNoLoginViewCommand command) {
        articleCacheRepository.incrArticleViewCount(command.getArticleId(), command.getIp());
    }

    public void articleNoLoginLikeIncrement(ArticleNoLoginLikeCommand command) {
        articleCacheRepository.incrArticleLikeCount(command.getArticleId(), command.getIp());
    }

    public UserArticleDataEntity articlesDataByUserId(String userId) {
        List<ArticlePO> articlePOList = articleRepository.countArticlesByUserId(userId);
        List<String> articleIds = articlePOList.stream()
                .map(ArticlePO::getArticleId)
                .collect(Collectors.toList());
        Long articleCount = (long) articlePOList.size();
        Map<String, Long> articleLikeCountBatch =
                articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Long likeCount = articleLikeCountBatch.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        Map<String, Long> articleViewCountBatch =
                articleCacheRepository.getArticleViewCountBatch(articleIds);
        Long viewCount = articleViewCountBatch.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        return new UserArticleDataEntity(articleCount, likeCount, viewCount);
    }

    public Long countUserArticlesPage(ArticleUserPageQuery query) {
        return articleRepository.countUserArticlesPage(query);
    }

    public List<ArticleEntity> getUserArticlesPage(ArticleUserPageQuery query) {
        List<ArticlePO> articlePOList = articleRepository.getUserArticlesPage(query,
                query.getOffset(), query.getLimit());
        if (articlePOList == null) {
            return new ArrayList<>();
        }

        List<ArticleEntity> articleEntityList = ArticleConvertor.toArticleEntityListConvertor(articlePOList);

        // 为每篇文章设置对应的标签列表
        setLabelListForArticleEntityList(articleEntityList);

        // 返回分页结果
        return articleEntityList;
    }

    public Map<String, ArticleEntity> getArticleByIds(List<String> articleIdList) {
        List<ArticlePO> articlePOList = articleRepository.getArticleByIds(articleIdList);
        Map<String, ArticleEntity> articleMap = new HashMap<>();
        for (ArticlePO articlePO : articlePOList) {
            ArticleEntity articleEntity = ArticleEntity.fromPO(articlePO);
            articleMap.put(articleEntity.getArticleId(), articleEntity);
        }
        return articleMap;
    }

}
