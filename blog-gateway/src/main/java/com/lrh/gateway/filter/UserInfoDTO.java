package com.lrh.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.context
 * @ClassName: UserInfoDTO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 16:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

    private String userId;
    private String userName;
    private String roleName;
    private String token;
}
