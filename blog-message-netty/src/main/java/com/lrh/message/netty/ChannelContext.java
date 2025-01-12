package com.lrh.message.netty;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.blog.message.netty
 * @ClassName: ChannelContext
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/11 23:14
 */
@Slf4j
public class ChannelContext {

    private final static Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static void addChannel(String userId, Channel channel) {
        CHANNEL_MAP.put(userId, channel);
        channel.attr(Attributes.USERID).set(userId);
    }

    public static Channel getChannel(String userId) {
        return CHANNEL_MAP.get(userId);
    }

    public static void removeChannel(Channel channel) {
        CHANNEL_MAP.remove(getUserId(channel));
        channel.attr(Attributes.USERID).set(null);
    }

    public static String getUserId(Channel channel){
        return channel.attr(Attributes.USERID).get();
    }


}
