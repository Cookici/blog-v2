package com.lrh.message.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto.req
 * @ClassName: FriendPageReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 17:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendPageReq extends PageReq {

    private String userId;

}
