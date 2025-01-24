package com.lrh.message.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto.req
 * @ClassName: MessageGetReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/22 19:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagePageReq extends PageReq {

    private String userId;

    private String toUserId;

}
