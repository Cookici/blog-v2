package com.lrh.message.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto.req
 * @ClassName: FriendUpdateReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 01:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyUpdateReq {

    private String userId;
    private String appliedId;
    private String applyStatus;
    private String friendName;

}
