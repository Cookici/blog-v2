package com.lrh.blog.user.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto
 * @ClassName: UserVO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 23:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private String userId;
    private String userName;
    private String userPhoto;
    private Integer userLevel;
}
