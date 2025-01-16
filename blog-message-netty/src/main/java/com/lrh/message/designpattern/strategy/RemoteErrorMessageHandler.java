package com.lrh.message.designpattern.strategy;

import com.lrh.message.config.designpattern.strategy.AbstractExecuteStrategy;
import com.lrh.message.enums.MessageTypeEnum;
import com.lrh.message.model.MessageModel;
import com.lrh.message.netty.ChannelContext;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageHandler;
import com.lrh.message.netty.message.MessageVO;
import com.lrh.message.service.impl.ThreadPoolService;
import com.lrh.message.utils.MessageUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.designpattern.strategy
 * @ClassName: RemoteErrorMessageHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/15 00:16
 */

@Slf4j
@Service
public class RemoteErrorMessageHandler  extends AbstractMessageHandler implements AbstractExecuteStrategy<MessageHandler, Void> {

    private final ThreadPoolService threadPoolService;

    public RemoteErrorMessageHandler(ThreadPoolService threadPoolService) {
        this.threadPoolService = threadPoolService;
    }

    @Override
    protected void setCache(MessageModel messageModel) {

    }

    @Override
    public void processMessage(MessageHandler messageHandler) {
        MessageDTO messageDTO = messageHandler.getMessageDTO();
        Channel channel = ChannelContext.getChannel(messageDTO.getToUserId());
        if (channel == null) {
            log.info("[WebSocketServer] 用户: {} 不在线", messageDTO.getToUserId());
            threadPoolService.setNoOnlineMessageCache(MessageUtil.convertMessageDTOToMessageModel(messageDTO));
            return;
        }
        MessageModel messageModel = MessageUtil.convertMessageDTOToMessageModel(messageDTO);
        try {
            MessageVO message = MessageUtil.convertMessageDTOToMessageVO(messageDTO);
            channel.writeAndFlush(MessageUtil.getMessageToWebSocketFrame(channel, message));
        } catch (RuntimeException e) {
            log.error("[RemoteErrorMessageHandler] processMessage error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String mark() {
        return MessageTypeEnum.RemoteErrorMessage.getMessageType();
    }

    @Override
    public void execute(MessageHandler messageHandler) {
        processMessage(messageHandler);
    }

}
