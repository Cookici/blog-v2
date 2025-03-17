package com.lrh.article.infrastructure.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.application.cqe.article.ArticleUserPageQuery;
import com.lrh.article.infrastructure.po.ArticlePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.mapper
 * @ClassName: ArticleMapper
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 21:45
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticlePO> {
    Long selectCountPage(@Param("query") ArticlePageQuery query);

    List<ArticlePO> selectPageArticle(@Param("query") ArticlePageQuery query,
                                      @Param("offset") Long offset,
                                      @Param("limit") Long limit);

    Long selectUserCountPage(@Param("query") ArticleUserPageQuery query);

    List<ArticlePO> selectUserPageArticle(@Param("query") ArticleUserPageQuery query,
                                          @Param("offset") Long offset,
                                          @Param("limit") Long limit);

    /**
     * 批量更新文章指标数据
     * @param updateBatch 更新数据批次
     */
    void batchUpdateMetrics(@Param("list") List<Map<String, Object>> updateBatch);
}
