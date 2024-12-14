package com.lrh.article.application.dto.article;

import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.dto
 * @ClassName: ArticleDTO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:36
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDTO {

    private String articleId;
    private String userId;
    private String articleTitle;
    private String articleContent;
    private LocalDateTime createTime;

    private UserVO userInfo;

    public static ArticleDTO fromEntity(ArticleEntity articleEntity) {
        return new ArticleDTO(
                articleEntity.getArticleId(),
                articleEntity.getUserId(),
                articleEntity.getArticleTitle(),
                articleEntity.getArticleContent(),
                articleEntity.getCreateTime(),
                new UserVO()
        );
    }

}
