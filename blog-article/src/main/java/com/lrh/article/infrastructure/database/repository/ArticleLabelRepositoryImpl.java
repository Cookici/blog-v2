package com.lrh.article.infrastructure.database.repository;

import com.lrh.article.domain.repository.ArticleLabelOperateRepository;
import com.lrh.article.infrastructure.database.mapper.ArticleLabelMapper;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    @Autowired
    private ArticleLabelMapper articleLabelMapper;

    @Override
    public Map<String, List<String>> getArticleIdMapLableIdList(List<String> articleIdList) {
        List<ArticleLabelPO> articleLabelPOList = articleLabelMapper.getArticleIdToLabelIdMap(articleIdList);
        return articleLabelPOList.stream()
                .collect(Collectors.groupingBy(
                        ArticleLabelPO::getArticleId,
                        Collectors.mapping(ArticleLabelPO::getLabelId, Collectors.toList())
                ));
    }
}
