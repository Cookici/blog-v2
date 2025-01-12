package com.lrh.message.netty;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.lrh.message.netty.initializer.CustomChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
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

    private final EventLoopGroup boosGroup = new NioEventLoopGroup(1);

    private final EventLoopGroup workGroup = new NioEventLoopGroup();

    private final CustomChannelInitializer customChannelInitializer;

    private final NacosDiscoveryProperties nacosDiscoveryProperties;

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

    public NettyServer(CustomChannelInitializer customChannelInitializer, NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.customChannelInitializer = customChannelInitializer;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    public void run() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workGroup)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(customChannelInitializer);
            ChannelFuture future = bootstrap.bind(nettyPort).sync();
            log.info("[NettyServer] 服务器启动成功 port:{}", nettyPort);
            registerNamingService(nettyName, nettyPort);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("[NettyServer] run 启动失败", e);
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void destroy() {
        boosGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
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
            String hostAddress = getLocalHostExactAddress().getHostAddress();
            namingService.registerInstance(nettyName, hostAddress, nettyPort);
            log.info("[NettyServer] 注册服务成功 host:{},port:{}", hostAddress, nettyPort);
        } catch (Exception e) {
            log.info("[NettyServer] 注册服务失败 error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    private InetAddress getLocalHostExactAddress() {
        try {
            InetAddress candidateAddress = null;

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了 就是我们要找的
                            return inetAddr;
                        }

                        // 若不是site-local地址 那就记录下该地址当作候选
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }

                    }
                }
            }
            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
        } catch (Exception e) {
            log.info("[NettyServer] 获取本地IP地址失败 error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
