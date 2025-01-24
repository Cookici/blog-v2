package com.lrh.message.designpattern.strategy;

import com.lrh.message.config.designpattern.strategy.AbstractExecuteStrategy;
import com.lrh.message.constants.MessageConstant;
import com.lrh.message.enums.MessageTypeEnum;
import com.lrh.message.model.MessageModel;
import com.lrh.message.mq.producer.MessageProducer;
import com.lrh.message.netty.ChannelContext;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageHandler;
import com.lrh.message.netty.message.MessageVO;
import com.lrh.message.service.MessageService;
import com.lrh.message.service.impl.ThreadPoolService;
import com.lrh.message.utils.MessageUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.designpattern.strategy
 * @ClassName: TextMessageHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/12 19:03
 */

@Slf4j
@Service
public class TextMessageHandler extends AbstractMessageHandler implements AbstractExecuteStrategy<MessageHandler, Void> {

    private final MessageProducer messageProducer;

    private final ThreadPoolService threadPoolService;

    private final MessageService messageService;

    public TextMessageHandler(MessageProducer messageProducer,ThreadPoolService threadPoolService, MessageService messageService) {
        this.messageProducer = messageProducer;
        this.threadPoolService = threadPoolService;
        this.messageService = messageService;
    }

    @Override
    public void processMessage(MessageHandler messageHandler) {
        MessageDTO messageDTO = messageHandler.getMessageDTO();
        Channel channel = ChannelContext.getChannel(messageDTO.getToUserId());
        if (channel == null) {
            log.info("[WebSocketServer] 用户: {} 不在线", messageDTO.getToUserId());
            MessageVO message = MessageUtil.convertMessageDTOToMessageVO(messageDTO, MessageConstant.STATUS_OFFLINE);
            messageService.setCache(message);
            threadPoolService.submitTask(() -> {
                MessageModel messageModel = MessageUtil.convertMessageDTOToMessageModel(messageDTO, MessageConstant.STATUS_OFFLINE);
                messageProducer.syncSendMessage(messageModel);
            });
            return;
        }
        try {
            MessageVO message = MessageUtil.convertMessageDTOToMessageVO(messageDTO, MessageConstant.STATUS_ONLINE);
            messageService.setCache(message);
            channel.writeAndFlush(MessageUtil.getMessageToWebSocketFrame(channel,message));
        } catch (RuntimeException e) {
            log.error("[TextMessageHandler] processMessage error: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        threadPoolService.submitTask(() -> {
            MessageModel messageModel = MessageUtil.convertMessageDTOToMessageModel(messageDTO, MessageConstant.STATUS_ONLINE);
            messageProducer.syncSendMessage(messageModel);
        });
    }

    @Override
    public String mark() {
        return MessageTypeEnum.TextMessage.getMessageType();
    }

    @Override
    public void execute(MessageHandler messageHandler) {
        processMessage(messageHandler);
    }

}
