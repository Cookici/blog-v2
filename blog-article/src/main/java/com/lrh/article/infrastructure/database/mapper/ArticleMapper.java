package com.lrh.article.infrastructure.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.infrastructure.po.ArticlePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
