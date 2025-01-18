package com.lrh.message.netty;

import io.netty.channel.ChannelFuture;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.netty
 * @ClassName: NettyCommandLineRunner
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/12 02:13
 */

@Component
public class NettyCommandLineRunner implements CommandLineRunner {

    private final NettyServer nettyServer;

    public NettyCommandLineRunner(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }


    @Override
    @Async
    public void run(String... args) throws Exception {
        ChannelFuture future = nettyServer.run();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            nettyServer.destroy();
            nettyServer.removeFromRedis();
        }));

        future.channel().closeFuture().syncUninterruptibly();
    }
}
