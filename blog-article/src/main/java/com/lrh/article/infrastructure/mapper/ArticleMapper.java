package com.lrh.article.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.article.infrastructure.po.ArticlePO;
import org.apache.ibatis.annotations.Mapper;

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

}
