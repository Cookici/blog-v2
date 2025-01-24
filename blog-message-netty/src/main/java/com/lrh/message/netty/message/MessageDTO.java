package com.lrh.message.netty.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.websocket
 * @ClassName: MessageDTO
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 16:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private String messageId;

    private String messageTag;

    private String messageType;

    private String messageContent;

    private String toUserId;

    private String userId;

    private Long timestamp;


}
