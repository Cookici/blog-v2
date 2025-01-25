package com.lrh.message.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto.req
 * @ClassName: MessageGetOfflineReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/25 18:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageGetOfflineReq {
    private List<String> userIds;
    private String userId;
}
