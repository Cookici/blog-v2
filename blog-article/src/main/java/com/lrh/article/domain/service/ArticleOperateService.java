package com.lrh.article.domain.service;

import com.lrh.article.application.cqe.article.*;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.repository.ArticleLabelOperateRepository;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.domain.repository.LabelOperateRepository;
import com.lrh.article.infrastructure.database.convertor.ArticleConvertor;
import com.lrh.article.infrastructure.database.convertor.LabelConvertor;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import com.lrh.article.infrastructure.po.ArticlePO;
import com.lrh.article.infrastructure.po.LabelPO;
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

    public ArticleOperateService(ArticleOperateRepository articleRepository, ArticleLabelOperateRepository articleLabelOperateRepository, LabelOperateRepository labelOperateRepository) {
        this.articleRepository = articleRepository;
        this.articleLabelOperateRepository = articleLabelOperateRepository;
        this.labelOperateRepository = labelOperateRepository;
    }


    public List<ArticleEntity> getArticlesPage(ArticlePageQuery articlePageQuery) {
        List<ArticlePO> articlePOList = articleRepository.getArticlesPage(articlePageQuery,
                articlePageQuery.getOffset(), articlePageQuery.getLimit());
        if (articlePOList == null) {
            return new ArrayList<>();
        }

        List<ArticleEntity> articleEntityList = ArticleConvertor.toArticleEntityListConvertor(articlePOList);

        // 提取文章 ID 列表
        List<String> articleIdList = articleEntityList.stream()
                .map(ArticleEntity::getArticleId)
                .collect(Collectors.toList());

        // 获取文章与标签 ID 的映射关系
        List<ArticleLabelPO> articleLabelPOList = articleLabelOperateRepository.getArticleIdMapLableIdList(articleIdList);
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

        // 返回分页结果
        return articleEntityList;
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

    public void deleteArticleById(ArticleDeleteCommand command) {
        articleLabelOperateRepository.deleteLabelForArticle(command.getArticleId());
        articleRepository.deleteArticleById(command.getArticleId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateArticleById(ArticleUpdateCommand command) {
        articleRepository.updateArticleById(command.getArticleId(), command.getArticleTitle(), command.getArticleContent());
        if (command.getLabelIdList().isEmpty()) {
            return;
        }
        articleLabelOperateRepository.deleteLabelForArticle(command.getArticleId());
        articleLabelOperateRepository.restoreDeletedArticleLabel(command.getArticleId(), command.getLabelIdList());
        articleLabelOperateRepository.upsertLabelForArticle(command.getArticleId(), command.getLabelIdList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertArticle(ArticleInsertCommand command) {
        ArticlePO articlePO = articleRepository.insertArticle(command);
        if (command.getLabelIdList().isEmpty()) {
            return;
        }
        articleLabelOperateRepository.upsertLabelForArticle(articlePO.getArticleId(), command.getLabelIdList());
    }
}
