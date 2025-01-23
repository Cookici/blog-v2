package com.lrh.article.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.domain.entity
 * @ClassName: UserArticleDataEntity
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/20 20:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserArticleDataEntity {

    private Long articleCount;
    private Long likeCount;
    private Long viewCount;

}
