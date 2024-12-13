package com.lrh.blog.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto.req
 * @ClassName: UserLoginReq
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午2:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginReq {

    private String userName;

    private String userPassword;

}
