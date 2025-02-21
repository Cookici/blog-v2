package com.lrh.article.infrastructure.database.mapper;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.infrastructure.database.mapper
 * @ClassName: ArticleLikeMapper
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/26 20:42
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.article.infrastructure.po.ArticleLikePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLikePO> {

}
