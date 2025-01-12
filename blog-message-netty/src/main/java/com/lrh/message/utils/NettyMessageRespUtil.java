package com.lrh.message.utils;

import com.alibaba.fastjson.JSON;
import com.lrh.message.netty.message.MessageVO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.utils
 * @ClassName: NettyMessageRespUtil
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/12 22:03
 */
@Slf4j
public class NettyMessageRespUtil {

    public static TextWebSocketFrame getMessageToWebSocketFrame(Channel channel, MessageVO messageVO){
        ByteBuf byteBuf = channel.alloc().buffer();
        String messageJsonString = JSON.toJSONString(messageVO);
        byte[] bytes = messageJsonString.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeBytes(bytes);
        return new TextWebSocketFrame(byteBuf);
    }

}
