package com.lrh.article.domain.service;

import com.lrh.article.application.cqe.article.*;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.entity.UserArticleDataEntity;
import com.lrh.article.domain.repository.*;
import com.lrh.article.domain.vo.ArticleMessageVO;
import com.lrh.article.infrastructure.client.OssClient;
import com.lrh.article.infrastructure.database.convertor.ArticleConvertor;
import com.lrh.article.infrastructure.database.convertor.LabelConvertor;
import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import com.lrh.article.infrastructure.po.ArticlePO;
import com.lrh.article.infrastructure.po.LabelPO;
import com.lrh.article.util.LockUtil;
import com.lrh.common.annotations.ArticleSyncRecords;
import com.lrh.common.context.UserContext;
import com.lrh.common.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.lrh.article.domain.vo.ArticleStatusEnum.*;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.service
 * @ClassName: ArticleService
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:24
 */
@Slf4j
@Service
public class ArticleOperateService {
    private final ArticleOperateRepository articleRepository;
    private final ArticleLabelOperateRepository articleLabelOperateRepository;
    private final LabelOperateRepository labelOperateRepository;
    private final CommentOperateRepository commentOperateRepository;
    private final ArticleCacheRepository articleCacheRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final RedissonClient redissonClient;
    private final OssClient ossClient;

    public ArticleOperateService(ArticleOperateRepository articleRepository, ArticleLabelOperateRepository articleLabelOperateRepository,
                                 LabelOperateRepository labelOperateRepository,
                                 OssClient ossClient,
                                 CommentOperateRepository commentOperateRepository,
                                 ArticleCacheRepository articleCacheRepository, ArticleLikeRepository articleLikeRepository, RedissonClient redissonClient) {

        this.articleRepository = articleRepository;
        this.articleLabelOperateRepository = articleLabelOperateRepository;
        this.labelOperateRepository = labelOperateRepository;
        this.commentOperateRepository = commentOperateRepository;
        this.articleCacheRepository = articleCacheRepository;

        this.ossClient = ossClient;
        this.articleLikeRepository = articleLikeRepository;

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
    public void syncUpdateArticle(String articleId) {
        ArticleEntity articleEntity = getArticleById(new ArticleQuery(articleId));
        if (articleEntity == null) {
            return;
        }
        // TODO 内容检测
        articleRepository.updateArticleSatusById(articleId, Published.getStatus());
        ArticleDO articleDO = ArticleDO.fromArticleEntity(articleEntity);
        articleRepository.saveArticleDo(articleDO);
        log.info("消费成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @ArticleSyncRecords
    public ArticleMessageVO deleteArticleById(ArticleDeleteCommand command) {
        validExceptionOperate(command.getArticleId(), command.getUserId());
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.tryWriteLock(String.format(RedisConstant.ARTICLE_LOCK, command.getArticleId()), () -> {
            articleRepository.updateArticleSatusById(command.getArticleId(), Deleted.getStatus());
        });
        return new ArticleMessageVO(command.getArticleId(), Deleted);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String ArticleId) {
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.tryWriteLock(String.format(RedisConstant.ARTICLE_LOCK, ArticleId), () -> {
            articleLabelOperateRepository.deleteLabelForArticle(ArticleId);
            Integer update = articleRepository.deleteArticleById(ArticleId);
            if (update == null || update == 0) {
                return;
            }
            commentOperateRepository.deleteCommentsByArticle(ArticleId);
            articleCacheRepository.deleteArticleCache(ArticleId);
            articleRepository.deleteEsById(ArticleId);
        });
    }

    private void validExceptionOperate(String articleId, String userId) {
        ArticlePO articlePO = articleRepository.getArticlesById(articleId);
        if (articlePO == null || !Objects.equals(articlePO.getUserId(), userId)) {
            throw new RuntimeException("非法操作");
        }
        if (Objects.equals(articlePO.getStatus(), UnderAudit.getStatus())) {
            throw new RuntimeException("审核中博客不允许修改");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @ArticleSyncRecords
    public ArticleMessageVO updateArticleById(ArticleUpdateCommand command) {
        validExceptionOperate(command.getArticleId(), command.getUserId());
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.tryWriteLock(String.format(RedisConstant.ARTICLE_LOCK, command.getArticleId()), () -> {
            articleRepository.updateArticleById(command.getArticleId(), command.getArticleTitle(), command.getArticleContent(), UnderAudit.getStatus());
            if (command.getLabelIdList().isEmpty()) {
                return;
            }
            articleLabelOperateRepository.deleteLabelForArticle(command.getArticleId());
            articleLabelOperateRepository.restoreDeletedArticleLabel(command.getArticleId(), command.getLabelIdList());
            articleLabelOperateRepository.upsertLabelForArticle(command.getArticleId(), command.getLabelIdList());
        });
        return new ArticleMessageVO(command.getArticleId(), UnderAudit);
    }

    @Transactional(rollbackFor = Exception.class)
    @ArticleSyncRecords
    public ArticleMessageVO insertArticle(ArticleInsertCommand command) {
        ArticlePO articlePO = ArticlePO.builder()
                                       .articleId("article_" + IdUtil.getUuid())
                                       .articleTitle(command.getArticleTitle())
                                       .articleContent(command.getArticleContent())
                                       .userId(command.getUserId())
                                       .status(UnderAudit.getStatus())
                                       .build();
        articleRepository.insertArticle(articlePO);
        if (command.getLabelIdList().isEmpty()) {
            return new ArticleMessageVO(articlePO.getArticleId(), UnderAudit);
        }
        articleLabelOperateRepository.upsertLabelForArticle(articlePO.getArticleId(), command.getLabelIdList());
        return new ArticleMessageVO(articlePO.getArticleId(), UnderAudit);
    }

    public void articleViewIncrement(ArticleViewCommand command) {
        articleCacheRepository.incrArticleViewCount(command.getArticleId(), UserContext.getUserId());
    }

    public void articleLikeIncrement(ArticleLikeCommand command) {
        Boolean isSuccess = articleCacheRepository.incrArticleLikeCount(command.getArticleId(), UserContext.getUserId());
        if (isSuccess) {
            try {
                articleLikeRepository.incrArticleLikeCount(command.getArticleId(), UserContext.getUserId());
            } catch (Exception e) {
                articleCacheRepository.deleteArticleLike(command.getArticleId(), UserContext.getUserId());
                throw new RuntimeException(e);
            }
        }
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
        if (articleIdList.isEmpty()) {
            return new HashMap<>();
        }
        List<ArticlePO> articlePOList = articleRepository.getArticleByIds(articleIdList);
        Map<String, ArticleEntity> articleMap = new HashMap<>();
        for (ArticlePO articlePO : articlePOList) {
            ArticleEntity articleEntity = ArticleEntity.fromPO(articlePO);
            articleMap.put(articleEntity.getArticleId(), articleEntity);
        }
        return articleMap;
    }

    public Page<ArticleEntity> queryListArticle(ArticleListQuery query) {
        // 查询文章列表并返回分页结果
        Page<ArticleDO> articleDOPage = articleRepository.findArticleListByQuery(query);

        // 将 ArticleDO 转换为 ArticleEntity
        List<ArticleEntity> articleEntityList = articleDOPage.getContent().stream()
                                                             .map(ArticleDO::toArticleEntity)
                                                             .collect(Collectors.toList());

        // 创建 PageImpl 并传递原始的 Pageable 和总数
        return new PageImpl<>(
                articleEntityList,
                articleDOPage.getPageable(),
                articleDOPage.getTotalElements()
        );

    }

    public void deleteArticleLike(String articleId, String userId) {
        Boolean isSuccess = articleCacheRepository.deleteArticleLike(articleId, userId);
        if (isSuccess) {
            try {
                articleLikeRepository.deleteArticleLike(articleId, userId);
            } catch (Exception e) {
                articleCacheRepository.incrArticleLikeCount(articleId, userId);
                throw new RuntimeException(e);
            }
        }
    }

}
