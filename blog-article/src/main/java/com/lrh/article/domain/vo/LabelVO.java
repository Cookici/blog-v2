package com.lrh.article.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.vo
 * @ClassName: ArticleVO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/15 16:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LabelVO {
    private String labelName;
    private String labelAlias;
}
