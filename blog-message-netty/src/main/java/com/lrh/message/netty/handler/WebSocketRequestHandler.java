package com.lrh.message.netty.handler;

import com.alibaba.fastjson2.JSONObject;
import com.lrh.message.config.NettyConfig;
import com.lrh.message.config.designpattern.strategy.AbstractStrategyChoose;
import com.lrh.message.enums.MessageTypeEnum;
import com.lrh.message.netty.Attributes;
import com.lrh.message.netty.ChannelContext;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageHandler;
import com.lrh.message.netty.message.MessageVO;
import com.lrh.message.utils.NettyMessageRespUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.blog.message.netty.handler
 * @ClassName: WebSocketRequestHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/11 23:05
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketRequestHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final AbstractStrategyChoose abstractStrategyChoose;

    public WebSocketRequestHandler(AbstractStrategyChoose abstractStrategyChoose) {
        this.abstractStrategyChoose = abstractStrategyChoose;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        MessageDTO messageDTO = null;
        try {
            messageDTO = getMessageDTO(ctx.channel(), msg);
            if (messageDTO == null) {
                return;
            }
            handlerMessage(messageDTO, ctx.channel());
        } catch (Exception e) {
            if (messageDTO != null) {
                handlerError(ctx.channel(), messageDTO);
            }
            throw new RuntimeException(e);
        }
    }

    private void handlerError(Channel channel, MessageDTO messageDTO) {
        if (channel.isOpen()) {
            MessageVO messageVO = new MessageVO();
            messageVO.setMessageType(MessageTypeEnum.ErrorMessage.getMessageType());
            messageVO.setMessageContent(messageDTO.getMessageTag());
            messageVO.setUserId(messageDTO.getUserId());
            channel.writeAndFlush(NettyMessageRespUtil.getMessageToWebSocketFrame(channel, messageVO));
        }
    }


    /**
     * 策略模式处理消息
     *
     * @param messageDTO 可操作消息
     */
    private void handlerMessage(MessageDTO messageDTO, Channel channel) {
        MessageHandler messageHandler = new MessageHandler(messageDTO, channel);
        abstractStrategyChoose.chooseAndExecute(messageDTO.getMessageType(), messageHandler);
    }


    /**
     * 转化原始消息为可操作消息
     *
     * @param msg 原始消息
     * @return 可操作消息
     */
    private MessageDTO getMessageDTO(Channel channel, WebSocketFrame msg) {
        if (msg == null || msg.toString().isEmpty()) {
            log.warn("[WebSocketServer] 收到空消息，忽略处理。");
            return null;
        }
        ByteBuf byteBuf = msg.content();
        String message = byteBuf.toString(StandardCharsets.UTF_8);
        MessageDTO messageDTO = JSONObject.parseObject(message, MessageDTO.class);
        if (messageDTO == null) {
            log.warn("[WebSocketServer] 消息解析为空，忽略处理。");
            return null;
        }
        String userId = ChannelContext.getUserId(channel);
        if (userId == null) {
            throw new RuntimeException();
        }
        messageDTO.setUserId(userId);
        return messageDTO;
    }

    /**
     * 客户端与服务端断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String userId = ctx.channel().attr(Attributes.USERID).get();
        if (userId == null) {
            throw new RuntimeException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        ChannelContext.removeChannel(ctx.channel());
        NettyConfig.group.remove(ctx.channel());
        log.info("[WebSocketServer] channelInactive 断开连接:{}", userId);
    }

    /**
     * 接收结束之后 read相对于服务端
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isOpen()) {
            ctx.channel().flush();
            ctx.channel().close();
        }
        log.info("[WebSocketServer] exceptionCaught error : {}", cause.getMessage(), cause);
    }


}
