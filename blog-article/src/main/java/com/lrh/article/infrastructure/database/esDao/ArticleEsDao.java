package com.lrh.article.infrastructure.database.esDao;

import com.lrh.article.application.cqe.article.ArticleListQuery;
import com.lrh.article.infrastructure.doc.ArticleDO;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.domain.PageImpl;  // 用来转换成 Page
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

@Repository
public class ArticleEsDao {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;


    public ArticleEsDao(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    // 模糊查询标题、正文和标签
    public Page<ArticleDO> searchArticles(ArticleListQuery query) {
        // 多字段模糊查询，查询标题、正文
        QueryBuilder searchQuery = QueryBuilders.multiMatchQuery(query.getElement(), "articleTitle", "articleContent")  // 多字段模糊查询
                                                .fuzziness("AUTO")  // 自动模糊度，适应不同的单词长度
                                                .operator(Operator.OR);  // 设置查询操作符，使用“或”操作符

        // 标签模糊查询
        QueryBuilder labelSearchQuery = QueryBuilders.wildcardQuery("labels", "*" + query.getElement() + "*");  // 标签字段模糊查询

        // 合并查询条件
        QueryBuilder combinedQuery = QueryBuilders.boolQuery().should(searchQuery)  // 标题和正文的模糊查询
                                                  .should(labelSearchQuery);  // 标签的模糊查询

        // 设置分页
        Pageable pageable = PageRequest.of(Math.toIntExact(query.getPage() - 1), Math.toIntExact(query.getPageSize()));

        // 创建 NativeSearchQuery 查询对象
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(combinedQuery);
        nativeSearchQuery.setPageable(pageable);  // 设置分页信息

        // 执行查询并返回 SearchHits
        SearchHits<ArticleDO> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, ArticleDO.class);

        // 将 SearchHits 转换为 Page
        return new PageImpl<>(searchHits.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList()), pageable, searchHits.getTotalHits());
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