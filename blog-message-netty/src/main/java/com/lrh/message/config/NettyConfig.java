package com.lrh.message.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.config
 * @ClassName: NettyConfig
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/12 19:12
 */
@Configuration
public class NettyConfig {
    /**
     * 储存每个客户端接入进来的channel对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
