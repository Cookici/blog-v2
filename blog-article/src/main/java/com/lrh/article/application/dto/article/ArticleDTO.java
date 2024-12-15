package com.lrh.article.application.dto.article;

import com.lrh.article.application.dto.label.LabelDTO;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime updateTime;

    private UserVO userInfo;

    private List<LabelDTO> labels;

    public static ArticleDTO fromEntity(ArticleEntity articleEntity, UserVO userVO) {
        return ArticleDTO.builder()
                .articleId(articleEntity.getArticleId())
                .userId(articleEntity.getUserId())
                .articleTitle(articleEntity.getArticleTitle())
                .articleContent(articleEntity.getArticleContent())
                .createTime(articleEntity.getCreateTime())
                .updateTime(articleEntity.getUpdateTime())
                .userInfo(userVO)
                .labels(LabelDTO.fromEntityList(articleEntity.getLabelEntityList()))
                .build();
    }

}
