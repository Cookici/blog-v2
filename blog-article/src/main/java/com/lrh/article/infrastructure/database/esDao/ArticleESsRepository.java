package com.lrh.article.infrastructure.database.esDao;

import com.lrh.article.infrastructure.doc.ArticleDO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleESsRepository extends ElasticsearchRepository<ArticleDO, String> {

}
