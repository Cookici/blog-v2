package com.lrh.article.infrastructure.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.article.infrastructure.po.ArticleLabelPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.database.mapper
 * @ClassName: ArticleLabelMapper
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 12:44
 */
@Mapper
public interface ArticleLabelMapper extends BaseMapper<ArticleLabelPO> {
    void batchUpsert(@Param("articleLabelPOList") List<ArticleLabelPO> articleLabelPOList);

    void restoreDeleted(@Param("articleId")String articleId,@Param("labelIdList") List<String> labelIdList);

    @Select({
            "<script>",
            "SELECT *",
            "FROM t_article_label",
            "WHERE article_id IN ",
            "<foreach collection='articleIdList' item='articleId' open='(' separator=',' close=')'>",
            "#{articleId}",
            "</foreach>",
            "AND is_deleted = 0",
            "</script>"
    })
    List<ArticleLabelPO> getArticleIdToLabelIdMap(@Param("articleIdList") List<String> articleIdList);
}
