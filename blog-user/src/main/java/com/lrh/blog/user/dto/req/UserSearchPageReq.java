package com.lrh.blog.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.blog.user.dto.req
 * @ClassName: UserPageRes
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 20:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchPageReq extends PageReq{

    private String keyword;

}
