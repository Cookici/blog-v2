package com.lrh.article.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.domain.entity
 * @ClassName: ArticleLikeEntity
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/26 21:19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikeEntity {

    private String articleId;
    private String userId;

}
