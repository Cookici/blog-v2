package com.lrh.message.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.controller
 * @ClassName: FriendUpdateNameReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 17:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendUpdateNameReq {
    private String userId;

    private String friendId;

    private String friendName;
}
