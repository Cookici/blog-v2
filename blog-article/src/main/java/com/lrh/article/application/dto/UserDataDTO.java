package com.lrh.article.application.dto;

import com.lrh.article.domain.entity.UserArticleDataEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.application.dto
 * @ClassName: UserArticleDataDTO
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/20 20:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataDTO {

    private Long articleCount;
    private Long likeCount;
    private Long viewCount;
    private Long commentCount;

    public static UserDataDTO fromEntity(UserArticleDataEntity userArticleData, Long commentCount) {
        return new UserDataDTO(
                userArticleData.getArticleCount(),
                userArticleData.getLikeCount(),
                userArticleData.getViewCount(),
                commentCount
        );
    }
}
