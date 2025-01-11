package com.lrh.message.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lrh.common.constant.PasswordKeyConstant;
import com.lrh.message.config.designpattern.strategy.AbstractStrategyChoose;
import com.lrh.message.enums.MessageTypeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.service
 * @ClassName: WebSocketServer
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/9 22:32
 */

@Getter
@Slf4j
@Component
@ServerEndpoint(value = "/api/message", configurator = CustomConfigurator.class)
public class WebSocketServer {

    private static AbstractStrategyChoose abstractStrategyChoose;

    private static final Map<String, WebSocketServer> SOCKET_MAP = new ConcurrentHashMap<>();

    private Session session;

    private String userId;

    @Autowired
    public void setAbstractStrategyChoose(AbstractStrategyChoose abstractStrategyChoose) {
        WebSocketServer.abstractStrategyChoose = abstractStrategyChoose;
    }


    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        String userId = (String) config.getUserProperties().get(PasswordKeyConstant.HEADER_USER_ID);
        this.session = session;
        this.userId = userId;
        SOCKET_MAP.put(userId, this);
        log.info("[WebSocketServer] 用户ID: {}, 连接成功", userId);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        if (this.userId != null) {
            SOCKET_MAP.remove(this.userId);
            log.info("[WebSocketServer] 用户ID: {}, 断开连接", this.userId);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("[WebSocketServer] 收到用户ID: {} 的消息: {},开始处理", this.userId, message);
        MessageDTO messageDTO = getMessageModel(message);
        if (messageDTO == null) {
            return;
        }
        try {
            handlerMessage(messageDTO);
        } catch (Exception e) {
            log.error("[WebSocketServer] 处理消息时发生错误: {}, 消息内容: {}", e.getMessage(), message, e);
            handlerError(session, messageDTO);
            throw new RuntimeException(e);
        }
    }


    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[WebSocketServer] 发生错误: {}, 用户ID: {}, 错误信息: {}", error, this.userId, error.getMessage(), error);
        closeSession(session);
    }

    private void closeSession(Session session) {
        try {
            if (session.isOpen()) {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "服务器错误"));
            }
        } catch (IOException e) {
            log.error("[WebSocketServer] 关闭会话时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 转化原始消息为可操作消息
     *
     * @param message 原始消息
     * @return 可操作消息
     */
    private MessageDTO getMessageModel(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("[WebSocketServer] 收到空消息，忽略处理。");
            return null;
        }
        MessageDTO messageDTO = JSONObject.parseObject(message, MessageDTO.class);
        if (messageDTO == null) {
            log.warn("[WebSocketServer] 消息解析为空，忽略处理。");
            return null;
        }
        messageDTO.setUserId(this.userId);
        return messageDTO;
    }

    /**
     * 策略模式处理消息
     *
     * @param messageDTO 可操作消息
     */
    private void handlerMessage(MessageDTO messageDTO) {
        MessageHandler messageHandler = new MessageHandler(messageDTO, SOCKET_MAP);
        abstractStrategyChoose.chooseAndExecute(messageDTO.getMessageType(), messageHandler);
    }


    private void handlerError(Session session,MessageDTO messageDTO) {
        try {
            if (session.isOpen()) {
                MessageVO messageVO = new MessageVO();
                messageVO.setMessageType(MessageTypeEnum.ErrorMessage.getMessageType());
                messageVO.setMessageContent(messageDTO.getMessageTag());
                session.getBasicRemote().sendText(JSON.toJSONString(messageVO));
            }
        } catch (IOException e) {
            log.error("[WebSocketServer] 返回错误消息失败: {}", e.getMessage(), e);
        }
    }
}
