package com.lrh.message.netty.message;

import io.netty.channel.Channel;
import lombok.Data;


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

    private final Channel channel;

    public MessageHandler(MessageDTO messageDTO,Channel channel) {
        this.channel = channel;
        this.messageDTO = messageDTO;
    }

}
