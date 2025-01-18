package com.lrh.blog.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.blog.user.dto.cqe
 * @ClassName: UserUpdatePasswordReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/17 23:59
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePasswordReq {

    private String userId;

    private String userPassword;

    private String newUserPassword;

    private String confirmUserPassword;

}
