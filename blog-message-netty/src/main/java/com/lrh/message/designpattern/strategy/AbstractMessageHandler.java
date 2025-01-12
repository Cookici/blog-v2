package com.lrh.message.designpattern.strategy;

import com.lrh.message.model.MessageModel;
import com.lrh.message.netty.message.MessageHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
     * 线程池配置
     */
    protected static final ExecutorService executorService = new ThreadPoolExecutor(
            10,
            20,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 设置Cache
     *
     * @param messageModel 消息模型
     */
    protected abstract void setCache(MessageModel messageModel);

    /**
     * 处理消息抽象接口
     *
     * @param messageHandler 消息请求参数
     */
    public abstract void processMessage(MessageHandler messageHandler);
}
