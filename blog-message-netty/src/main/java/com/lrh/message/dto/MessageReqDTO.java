package com.lrh.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto
 * @ClassName: MessageReqDTO
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 16:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReqDTO {
    private String userId;
    private String toUserId;
    private String messageId;
    private Long timestamp;
}
