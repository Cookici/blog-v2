package com.lrh.message.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto.req
 * @ClassName: FriendApplyPageReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 01:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyPageReq extends PageReq{
    private String userId;
}
