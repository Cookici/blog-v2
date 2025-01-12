package com.lrh.message.netty.message;

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

    /**
     * 错误的消息类型内容为messageTag
     */
    private String messageContent;

    private String userId;

}
