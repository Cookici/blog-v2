package com.lrh.message.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto.req
 * @ClassName: FriendSearchPageReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 19:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendSearchPageReq extends PageReq{

    private String userId;

    private String keyword;

}
