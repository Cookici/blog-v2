package com.lrh.article.infrastructure.database.esDao;

import com.lrh.article.application.cqe.article.ArticleListQuery;
import com.lrh.article.infrastructure.doc.ArticleDO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

@Slf4j
@Repository
public class ArticleEsDao {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;


    public ArticleEsDao(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    /**
     * 模糊查询标题、正文和标签
     */
    public Page<ArticleDO> searchArticles(ArticleListQuery query) {
        // 多字段模糊查询，查询标题、正文
        QueryBuilder searchQuery = QueryBuilders.multiMatchQuery(query.getElement(), "articleTitle", "articleContent")
                                                .fuzziness("AUTO")
                                                .operator(Operator.OR);

        // 标签模糊查询
        QueryBuilder labelSearchQuery = QueryBuilders.wildcardQuery("labels", "*" + query.getElement() + "*");

        // is_deleted 精确匹配
        QueryBuilder isDeletedQuery = QueryBuilders.termQuery("is_deleted", 0);

        // 合并查询条件
        QueryBuilder combinedQuery = QueryBuilders.boolQuery()
                                                  .must(isDeletedQuery)          // 只查询未删除的
                                                  .should(searchQuery)          // 标题和正文的模糊查询
                                                  .should(labelSearchQuery)     // 标签的模糊查询
                                                  .minimumShouldMatch(1);       // 至少匹配一个 should 条件

        // 设置分页
        Pageable pageable = PageRequest.of(Math.toIntExact(query.getPage() - 1), Math.toIntExact(query.getPageSize()));

        // 创建 NativeSearchQuery 查询对象
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(combinedQuery);
        nativeSearchQuery.setPageable(pageable);

        // 执行查询并返回 SearchHits
        SearchHits<ArticleDO> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, ArticleDO.class);

        // 将 SearchHits 转换为 Page
        return new PageImpl<>(searchHits.getSearchHits().stream()
                                        .map(SearchHit::getContent)
                                        .collect(Collectors.toList()), pageable, searchHits.getTotalHits());
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
}