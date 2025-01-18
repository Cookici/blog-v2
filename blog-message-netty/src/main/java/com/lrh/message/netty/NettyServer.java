package com.lrh.message.netty;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.lrh.message.constants.RedisKeyConstant;
import com.lrh.message.netty.initializer.CustomChannelInitializer;
import com.lrh.message.utils.NettyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.blog.message.netty
 * @ClassName: NettyServer
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/11 22:58
 */
@Slf4j
@Component
public class NettyServer {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private final EventLoopGroup workGroup = new NioEventLoopGroup();

    private final CustomChannelInitializer customChannelInitializer;

    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Netty端口
     */
    @Value("${netty.port}")
    private Integer nettyPort;

    /**
     * Netty应用名称
     */
    @Value("${netty.application.name}")
    private String nettyName;

    private Channel channel;

    public NettyServer(CustomChannelInitializer customChannelInitializer, NacosDiscoveryProperties nacosDiscoveryProperties, RedisTemplate<String, Object> redisTemplate) {
        this.customChannelInitializer = customChannelInitializer;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        this.redisTemplate = redisTemplate;
    }

    public ChannelFuture run() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .channel(NioServerSocketChannel.class)
                .childHandler(customChannelInitializer);
        ChannelFuture future = bootstrap.bind(nettyPort);
        channel = future.channel();
        log.info("[NettyServer] 服务器启动成功 port:{}", nettyPort);
        registerNamingService(nettyName, nettyPort);
        redisTemplate.opsForSet().add(RedisKeyConstant.NETTY_SERVER_SET_KEY,
                NettyUtil.getLocalHostExactAddress().getHostAddress() + ":" + nettyPort);
        return future;
    }


    /**
     * 将Netty服务注册进Nacos
     *
     * @param nettyName 服务名称
     * @param nettyPort 服务端口号
     */
    private void registerNamingService(String nettyName, Integer nettyPort) {
        try {
            Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosDiscoveryProperties.getServerAddr());
            properties.setProperty(PropertyKeyConst.NAMESPACE, nacosDiscoveryProperties.getNamespace());
            NamingService namingService = NamingFactory.createNamingService(properties);
            String hostAddress = NettyUtil.getLocalHostExactAddress().getHostAddress();
            namingService.registerInstance(nettyName, hostAddress, nettyPort);
            log.info("[NettyServer] 注册服务成功 host:{},port:{}", hostAddress, nettyPort);
        } catch (Exception e) {
            log.info("[NettyServer] 注册服务失败 error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }


    public void removeFromRedis() {
        redisTemplate.opsForSet().remove(RedisKeyConstant.NETTY_SERVER_SET_KEY,
                NettyUtil.getLocalHostExactAddress().getHostAddress() + ":" + nettyPort);
        log.info("[NettyServer] redis移除服务成功");
    }
}
