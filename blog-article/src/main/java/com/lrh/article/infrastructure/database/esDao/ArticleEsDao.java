package com.lrh.article.infrastructure.database.esDao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lrh.article.application.cqe.article.ArticleListQuery;
import com.lrh.article.infrastructure.doc.ArticleDO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ArticleEsDao {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    private final ArticleESsRepository articleRepository;


    public ArticleEsDao(ElasticsearchRestTemplate elasticsearchRestTemplate, ArticleESsRepository articleRepository) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
        this.articleRepository = articleRepository;
    }

    /**
     * 模糊查询标题、正文和标签
     */
    public Page<ArticleDO> searchArticles(ArticleListQuery query) {
        // 多字段模糊查询，查询标题、正文
        // 多字段模糊查询
        QueryBuilder searchQuery = QueryBuilders.multiMatchQuery(query.getElement(), "articleTitle", "articleContent")
                                                // 自动模糊度，适应不同的单词长度
                                                .fuzziness("AUTO")
                                                // 设置查询操作符，使用“或”操作符
                                                .operator(Operator.OR);

        // 标签模糊查询
        // 标签字段模糊查询
        QueryBuilder labelSearchQuery = QueryBuilders.wildcardQuery("labels", "*" + query.getElement() + "*");

        // 合并查询条件
        // 标题和正文的模糊查询
        QueryBuilder combinedQuery = QueryBuilders.boolQuery().should(searchQuery)
                                                  // 标签的模糊查询
                                                  .should(labelSearchQuery);

        // 设置分页
        Pageable pageable = PageRequest.of(Math.toIntExact(query.getPage() - 1), Math.toIntExact(query.getPageSize()));

        // 创建 NativeSearchQuery 查询对象
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(combinedQuery);
        // 设置分页信息
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
        Criteria criteria = new Criteria("articleId").is(articleId);
        CriteriaQuery deleteQuery = new CriteriaQuery(criteria);
        elasticsearchRestTemplate.delete(deleteQuery, ArticleDO.class);
    }
}