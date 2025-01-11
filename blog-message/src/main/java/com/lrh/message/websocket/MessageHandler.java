package com.lrh.message.websocket;

import lombok.Data;

import java.util.Map;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.websocket
 * @ClassName: MessageHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 00:55
 */
@Data
public class MessageHandler {

    private final MessageDTO messageDTO;

    private final Map<String, WebSocketServer> socketMap;

    MessageHandler(MessageDTO messageDTO, Map<String, WebSocketServer> socketMap) {
        this.messageDTO = messageDTO;
        this.socketMap = socketMap;
    }

}
