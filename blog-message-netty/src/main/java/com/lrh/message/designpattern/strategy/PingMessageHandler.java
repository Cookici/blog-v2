package com.lrh.message.designpattern.strategy;

import com.lrh.message.config.designpattern.strategy.AbstractExecuteStrategy;
import com.lrh.message.constants.MessageConstant;
import com.lrh.message.enums.MessageTypeEnum;
import com.lrh.message.netty.ChannelContext;
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
 * @ClassName: PingMessageHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/22 13:40
 */
@Slf4j
@Service
public class PingMessageHandler extends AbstractMessageHandler implements AbstractExecuteStrategy<MessageHandler, Void> {

    @Override
    public void processMessage(MessageHandler messageHandler) {
        log.info("[WebSocketServer] 收到ping消息");
        MessageDTO messageDTO = messageHandler.getMessageDTO();
        Channel channel = ChannelContext.getChannel(messageDTO.getToUserId());
        if (channel == null) {
            log.info("[WebSocketServer] pong用户: {} 断连", messageDTO.getToUserId());
            return;
        }
        try {
            MessageVO message = MessageUtil.convertMessageDTOToMessageVO(messageDTO, MessageConstant.NO_STATUS);
            channel.writeAndFlush(MessageUtil.getMessageToWebSocketFrame(channel, message));
        } catch (RuntimeException e) {
            log.error("[PhotoMessageHandler] processMessage error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Override
    public String mark() {
        return MessageTypeEnum.PingMessage.getMessageType();
    }

    @Override
    public void execute(MessageHandler messageHandler) {
        processMessage(messageHandler);
    }

}
