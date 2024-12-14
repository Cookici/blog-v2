package com.lrh.article.application.dto.article;

import com.lrh.article.application.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.dto.article
 * @ClassName: ArticlePageDTO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 22:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticlePageDTO {
    private PageDTO<ArticleDTO> pageInfo;
}
