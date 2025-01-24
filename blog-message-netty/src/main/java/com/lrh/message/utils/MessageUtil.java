package com.lrh.message.utils;

import com.alibaba.fastjson.JSON;
import com.lrh.message.model.MessageModel;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageVO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.data.redis.core.ZSetOperations;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.utils
 * @ClassName: MessageUtil
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/14 14:07
 */

public class MessageUtil {
    public static TextWebSocketFrame getMessageToWebSocketFrame(Channel channel, MessageVO messageVO) {
        ByteBuf byteBuf = channel.alloc().buffer();
        String messageJsonString = JSON.toJSONString(messageVO);
        byte[] bytes = messageJsonString.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeBytes(bytes);
        return new TextWebSocketFrame(byteBuf);
    }

    public static MessageModel convertMessageDTOToMessageModel(MessageDTO messageDTO, String messageStatus) {
        MessageModel messageModel = new MessageModel();
        messageModel.setMessageId(messageDTO.getMessageId());
        messageModel.setMessageContent(messageDTO.getMessageContent());
        messageModel.setMessageType(messageDTO.getMessageType());
        messageModel.setUserId(messageDTO.getUserId());
        messageModel.setToUserId(messageDTO.getToUserId());
        messageModel.setTimestamp(messageDTO.getTimestamp());
        messageModel.setMessageStatus(messageStatus);
        return messageModel;
    }

    public static MessageVO convertMessageDTOToMessageVO(MessageDTO messageDTO, String messageStatus) {
        MessageVO message = new MessageVO();
        message.setMessageId(messageDTO.getMessageId());
        message.setMessageType(messageDTO.getMessageType());
        message.setMessageContent(messageDTO.getMessageContent());
        message.setUserId(messageDTO.getUserId());
        message.setToUserId(messageDTO.getToUserId());
        message.setTimestamp(messageDTO.getTimestamp());
        message.setMessageStatus(messageStatus);
        return message;
    }

    public static List<MessageVO> redisMessageConvertToMessageVO(Set<ZSetOperations.TypedTuple<Object>> typedTuples) {
        if (typedTuples == null || typedTuples.isEmpty()) {
            return new ArrayList<>();
        }


        return typedTuples.stream()
                .map(typedTuple -> {
                    MessageVO messageVO = JSON.parseObject(JSON.toJSONString(typedTuple.getValue()), MessageVO.class);
                    return messageVO;
                })
                .collect(Collectors.toList());
    }
}
