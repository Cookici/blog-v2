package com.lrh.article.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.domain.vo
 * @ClassName: UserVO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 21:08
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO {
    private String userId;
    private String userName;
    private String userPhoto;
    private Integer userLevel;
}
