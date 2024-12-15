package com.lrh.article.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.repository.ArticleLabelOperateRepository;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import com.lrh.article.domain.repository.LabelOperateRepository;
import org.springframework.stereotype.Service;

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
    private final LabelOperateRepository labelOperateRepository;
    private final ArticleLabelOperateRepository articleLabelOperateRepository;

    public ArticleOperateService(ArticleOperateRepository articleRepository, LabelOperateRepository labelOperateRepository, ArticleLabelOperateRepository articleLabelOperateRepository) {
        this.articleRepository = articleRepository;
        this.labelOperateRepository = labelOperateRepository;
        this.articleLabelOperateRepository = articleLabelOperateRepository;
    }


    public Page<ArticleEntity> getArticlesPage(ArticlePageQuery articlePageQuery) {
        // 获取分页的文章数据
        Page<ArticleEntity> articleEntityPage = articleRepository.getArticlesPage(articlePageQuery);
        List<ArticleEntity> articleEntityList = articleEntityPage.getRecords();

        if (articleEntityList.isEmpty()) {
            return articleEntityPage;
        }

        // 提取文章 ID 列表
        List<String> articleIdList = articleEntityList.stream()
                .map(ArticleEntity::getArticleId)
                .collect(Collectors.toList());

        // 获取文章与标签 ID 的映射关系
        Map<String, List<String>> articleIdToLabelIdsMap = articleLabelOperateRepository.getArticleIdMapLableIdList(articleIdList);

        // 获取标签详细信息
        List<LabelEntity> labelEntityList = labelOperateRepository.getLabelListByIds(
                articleIdToLabelIdsMap.values().stream()
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList())
        );

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
        return articleEntityPage;
    }

    /**
     * 计算文章的总数
     *
     * @return 总文章数
     */
    public Long countArticlesPage(ArticlePageQuery articlePageQuery) {
        return articleRepository.countArticlesPage(articlePageQuery);
    }
}
