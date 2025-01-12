package com.lrh.message.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.websocket
 * @ClassName: Message
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 00:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageVO {

    private String messageType;

    private String messageContent;

    private String messageTag;

    private String toUserId;

    private String userId;

}
