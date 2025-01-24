package com.lrh.message.netty.initializer;

import com.lrh.message.netty.handler.HttpRequestHandler;
import com.lrh.message.netty.handler.WebSocketRequestHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: Blog
 * @Package: com.lrh.blog.chat.server.handler
 * @ClassName: WebSocketChannelHandler
 * @Author: 63283
 * @Description:
 * @Date: 2025/01/11 17:35
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpRequestHandler httpRequestHandler;

    private final WebSocketRequestHandler webSocketRequestHandler;

    public CustomChannelInitializer(HttpRequestHandler httpRequestHandler, WebSocketRequestHandler webSocketRequestHandler) {
        this.httpRequestHandler = httpRequestHandler;
        this.webSocketRequestHandler = webSocketRequestHandler;
    }


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // HTTP 编解码器
        pipeline.addLast("httpServerCodec", new HttpServerCodec())
                .addLast("objectEncoder", new ObjectEncoder())
                .addLast("httpChunkedWriteHandler", new ChunkedWriteHandler())
                .addLast("httpObjectAggregator", new HttpObjectAggregator(65536))
                // 自定义的请求处理器，在这里获取 header 信息
                .addLast("httpRequestHandler", httpRequestHandler)
                // 空闲状态处理器
                .addLast("idleStateHandler", new IdleStateHandler(30, 30, 60, TimeUnit.MINUTES))
                // WebSocket 协议处理器，升级到 WebSocket
                .addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/api/netty",
                        null, true, 65536 * 10, false, true))
                // WebSocket 处理器，传递获取的 header 信息
                .addLast("webSocketHandler", webSocketRequestHandler);


    }
}
