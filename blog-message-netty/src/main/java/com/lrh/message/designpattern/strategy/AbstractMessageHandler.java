package com.lrh.message.designpattern.strategy;

import com.lrh.message.netty.message.MessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.designpattern.strategy
 * @ClassName: MessageProcessHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 00:33
 */
@Slf4j
public abstract class AbstractMessageHandler {

    /**
     * 处理消息抽象接口
     *
     * @param messageHandler 消息请求参数
     */
    public abstract void processMessage(MessageHandler messageHandler);
}
