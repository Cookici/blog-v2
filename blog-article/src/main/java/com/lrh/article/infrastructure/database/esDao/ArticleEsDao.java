package com.lrh.article.infrastructure.database.esDao;

import com.lrh.article.infrastructure.doc.ArticleDO;
import com.lrh.common.constant.BusinessConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ArticleEsDao {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;


    public ArticleEsDao(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }


    public void saveArticleDo(ArticleDO article) {
        elasticsearchRestTemplate.save(article);
    }


    public void deleteArticleById(String articleId) {
        ArticleDO article = elasticsearchRestTemplate.get(articleId, ArticleDO.class);
        if (article != null) {
            article.setIsDeleted(1);
            elasticsearchRestTemplate.save(article);
        }
    }

    public List<ArticleDO> getArticleList(Long offset, Long limit, String element) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();


        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED));

        // 如果有搜索关键词，添加到布尔查询中
        if (StringUtils.isNotBlank(element)) {
            boolQuery.should(QueryBuilders.matchQuery("articleTitle", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("articleContent", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("userName", element).fuzziness(Fuzziness.AUTO))
                    .minimumShouldMatch(1);
        }

        // 设置查询
        queryBuilder.withQuery(boolQuery);
        queryBuilder.withSorts(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(offset.intValue(), limit.intValue()));

        SearchHits<ArticleDO> searchHits = elasticsearchRestTemplate.search(
                queryBuilder.build(), ArticleDO.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public Long countArticle(String element) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();


        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED));

        // 如果有搜索关键词，添加到布尔查询中
        if (StringUtils.isNotBlank(element)) {
            boolQuery.should(QueryBuilders.matchQuery("articleTitle", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("articleContent", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("userName", element).fuzziness(Fuzziness.AUTO))
                    .minimumShouldMatch(1);
        }

        // 设置查询
        queryBuilder.withQuery(boolQuery);

        return elasticsearchRestTemplate.count(queryBuilder.build(), ArticleDO.class);
    }

    public Long countLikeArticleList(String element, Set<String> likeIds) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED))
                .must(QueryBuilders.termsQuery("articleId", likeIds));

        if (StringUtils.isNotBlank(element)) {
            boolQuery.should(QueryBuilders.matchQuery("articleTitle", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("articleContent", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("userName", element).fuzziness(Fuzziness.AUTO))
                    .minimumShouldMatch(1);
        }

        queryBuilder.withQuery(boolQuery);

        return elasticsearchRestTemplate.count(queryBuilder.build(), ArticleDO.class);
    }

    public List<ArticleDO> getLikeArticleList(Long offset, Long limit, String element, Set<String> likeIds) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED))
                .must(QueryBuilders.termsQuery("articleId", likeIds));

        if (StringUtils.isNotBlank(element)) {
            boolQuery.should(QueryBuilders.matchQuery("articleTitle", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("articleContent", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("userName", element).fuzziness(Fuzziness.AUTO))
                    .minimumShouldMatch(1);
        }

        queryBuilder.withQuery(boolQuery);
        queryBuilder.withSorts(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(offset.intValue(), limit.intValue()));

        SearchHits<ArticleDO> searchHits = elasticsearchRestTemplate.search(
                queryBuilder.build(), ArticleDO.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 更新文章指标数据
     *
     * @param articleId 文章ID
     * @param updateMap 更新数据
     */
    public void updateArticleMetrics(String articleId, Map<String, Object> updateMap) {
        ArticleDO article = elasticsearchRestTemplate.get(articleId, ArticleDO.class);
        if (article != null) {
            if (updateMap.containsKey("likeCount")) {
                article.setLikeCount((Long) updateMap.get("likeCount"));
            }
            if (updateMap.containsKey("viewCount")) {
                article.setViewCount((Long) updateMap.get("viewCount"));
            }
            elasticsearchRestTemplate.save(article);
            log.info("更新文章指标数据成功，articleId: {}, likeCount: {}, viewCount: {}",
                    articleId, article.getLikeCount(), article.getViewCount());
        }
    }

    public Long countUserArticlesEsPage(String userId, String element) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("userId", userId))
                .must(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED));

        if (StringUtils.isNotBlank(element)) {
            boolQuery.should(QueryBuilders.matchQuery("articleTitle", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("articleContent", element).fuzziness(Fuzziness.AUTO))
                    .minimumShouldMatch(1);
        }

        queryBuilder.withQuery(boolQuery);

        return elasticsearchRestTemplate.count(queryBuilder.build(), ArticleDO.class);
    }

    public List<ArticleDO> getUserArticlesEsPage(Long limit, Long offset, String userId, String element) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("userId", userId))
                .must(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED));

        if (StringUtils.isNotBlank(element)) {
            boolQuery.should(QueryBuilders.matchQuery("articleTitle", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("articleContent", element).fuzziness(Fuzziness.AUTO))
                    .minimumShouldMatch(1);
        }

        queryBuilder.withQuery(boolQuery);
        queryBuilder.withSorts(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(offset.intValue(), limit.intValue()));

        SearchHits<ArticleDO> searchHits = elasticsearchRestTemplate.search(
                queryBuilder.build(), ArticleDO.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<ArticleDO> getHotArticles(List<String> excludeArticleIds) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        if (excludeArticleIds != null && !excludeArticleIds.isEmpty()) {
            queryBuilder.withQuery(QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED))
                    .mustNot(QueryBuilders.termsQuery("articleId", excludeArticleIds)));
        } else {
            queryBuilder.withQuery(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED));
        }

        queryBuilder.withSorts(SortBuilders.fieldSort("likeCount").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(0, 10));

        SearchHits<ArticleDO> searchHits = elasticsearchRestTemplate.search(
                queryBuilder.build(), ArticleDO.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<ArticleDO> getHotArticlesTop(Integer top) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.termQuery("isDeleted", BusinessConstant.IS_NOT_DELETED));
        queryBuilder.withSorts(SortBuilders.fieldSort("likeCount").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(0, top));
        SearchHits<ArticleDO> searchHits = elasticsearchRestTemplate.search(
                queryBuilder.build(), ArticleDO.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取ES中的文章
     *
     * @param articleId 文章ID
     * @return 文章对象，如果不存在则返回null
     */
    public ArticleDO getArticleById(String articleId) {
        return elasticsearchRestTemplate.get(articleId, ArticleDO.class);
    }

    /**
     * 批量保存文章到ES
     * @param articles 文章列表
     * @return 成功保存的数量
     */
    public int batchSaveArticles(List<ArticleDO> articles) {
        if (articles == null || articles.isEmpty()) {
            return 0;
        }

        try {
            Iterable<ArticleDO> savedArticles = elasticsearchRestTemplate.save(articles);
            int count = 0;
            for (ArticleDO ignored : savedArticles) {
                count++;
            }
            log.info("批量保存文章到ES成功，共{}篇", count);
            return count;
        } catch (Exception e) {
            log.error("批量保存文章到ES失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 批量更新文章到ES
     * @param articles 文章列表
     * @return 成功更新的数量
     */
    public int batchUpdateArticles(List<ArticleDO> articles) {
        if (articles == null || articles.isEmpty()) {
            return 0;
        }

        try {
            Iterable<ArticleDO> updatedArticles = elasticsearchRestTemplate.save(articles);
            int count = 0;
            for (ArticleDO ignored : updatedArticles) {
                count++;
            }
            log.info("批量更新ES文章成功，共{}篇", count);
            return count;
        } catch (Exception e) {
            log.error("批量更新ES文章失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}