package com.lrh.message.designpattern.strategy;

import com.lrh.message.config.designpattern.strategy.AbstractExecuteStrategy;
import com.lrh.message.constants.MessageConstant;
import com.lrh.message.enums.MessageTypeEnum;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageHandler;
import com.lrh.message.netty.message.MessageVO;
import com.lrh.message.utils.MessageUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.designpattern.strategy
 * @ClassName: NotFriendMessageHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 17:34
 */
@Slf4j
@Service
public class NotFriendMessageHandler extends AbstractMessageHandler implements AbstractExecuteStrategy<MessageHandler, Void> {

    @Override
    public void execute(MessageHandler messageHandler) {
        processMessage(messageHandler);
    }


    @Override
    public void processMessage(MessageHandler messageHandler) {
        log.info("[WebSocketServer] NotFriendMessageHandler 收到消息");
        MessageDTO messageDTO = messageHandler.getMessageDTO();
        Channel channel = messageHandler.getChannel();
        if (channel == null) {
            return;
        }
        try {
            MessageVO message = MessageUtil.convertMessageDTOToMessageVO(messageDTO, MessageConstant.STATUS_ONLINE);
            channel.writeAndFlush(MessageUtil.getMessageToWebSocketFrame(channel, message));
        } catch (RuntimeException e) {
            log.error("[NotFriendMessageHandler] processMessage error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String mark() {
        return MessageTypeEnum.NotFriendMessage.getMessageType();
    }
}
