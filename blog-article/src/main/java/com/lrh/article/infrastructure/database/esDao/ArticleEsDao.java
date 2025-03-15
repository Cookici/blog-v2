package com.lrh.article.infrastructure.database.esDao;

import com.lrh.article.infrastructure.doc.ArticleDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.Fuzziness;
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

        if (StringUtils.isNotBlank(element)) {
            queryBuilder.withQuery(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("articleTitle", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("articleContent", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("userName", element).fuzziness(Fuzziness.AUTO)));
        }

        // 使用新的API替代已弃用的withSort方法
        queryBuilder.withSorts(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC));
        
        // 分页设置
        queryBuilder.withPageable(PageRequest.of(offset.intValue(), limit.intValue()));

        SearchHits<ArticleDO> searchHits = elasticsearchRestTemplate.search(
                queryBuilder.build(), ArticleDO.class);
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public Long countArticle(String element) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        if (StringUtils.isNotBlank(element)) {
            queryBuilder.withQuery(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("articleTitle", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("articleContent", element).fuzziness(Fuzziness.AUTO))
                    .should(QueryBuilders.matchQuery("userName", element).fuzziness(Fuzziness.AUTO)));
        }

        return elasticsearchRestTemplate.count(queryBuilder.build(), ArticleDO.class);
    }
}