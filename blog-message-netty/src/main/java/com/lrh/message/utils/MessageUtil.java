package com.lrh.message.utils;

import com.alibaba.fastjson.JSON;
import com.lrh.common.util.IdUtil;
import com.lrh.message.model.MessageModel;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageVO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.utils
 * @ClassName: MessageUtil
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/14 14:07
 */

public class MessageUtil {
    public static TextWebSocketFrame getMessageToWebSocketFrame(Channel channel, MessageVO messageVO){
        ByteBuf byteBuf = channel.alloc().buffer();
        String messageJsonString = JSON.toJSONString(messageVO);
        byte[] bytes = messageJsonString.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeBytes(bytes);
        return new TextWebSocketFrame(byteBuf);
    }

    public static MessageModel convertMessageDTOToMessageModel(MessageDTO messageDTO) {
        MessageModel messageModel = new MessageModel();
        messageModel.setMessageContent(messageDTO.getMessageContent());
        messageModel.setMessageType(messageDTO.getMessageType());
        messageModel.setUserId(messageDTO.getUserId());
        messageModel.setToUserId(messageDTO.getToUserId());
        messageModel.setTimestamp(messageDTO.getTimestamp());
        messageModel.setMessageId("message_" + IdUtil.getUuid());
        return messageModel;
    }

    public static MessageVO convertMessageDTOToMessageVO(MessageDTO messageDTO) {
        MessageVO message = new MessageVO();
        message.setMessageType(messageDTO.getMessageType());
        message.setMessageContent(messageDTO.getMessageContent());
        message.setUserId(messageDTO.getUserId());
        return message;
    }
}
