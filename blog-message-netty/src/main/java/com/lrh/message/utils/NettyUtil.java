package com.lrh.message.utils;

import com.lrh.message.enums.MessageTypeEnum;
import com.lrh.message.netty.message.MessageDTO;
import com.lrh.message.netty.message.MessageVO;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.utils
 * @ClassName: NettyMessageRespUtil
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/12 22:03
 */
@Slf4j
public class NettyUtil {

    public static void handlerError(Channel channel, MessageDTO messageDTO) {
        if (channel.isOpen()) {
            MessageVO messageVO = new MessageVO();
            messageVO.setMessageType(MessageTypeEnum.ErrorMessage.getMessageType());
            messageVO.setToUserId(messageDTO.getToUserId());
            messageVO.setUserId(messageDTO.getUserId());
            channel.writeAndFlush(MessageUtil.getMessageToWebSocketFrame(channel, messageVO));
        }
    }

    public static String getDynamicTopic(String remoteTopic,String nettyPort) {
        String host = getLocalHostExactAddress().getHostAddress() + ":"+nettyPort;
        return getDestTopic(host, remoteTopic);
    }

    public static String getDestTopic(String address,String remoteTopic) {
        String topic  = remoteTopic + address;
        return topic.replaceAll("\\.", "-").replaceAll(":","-");
    }

    public static String getGroupOnlyOne(String group,String nettyPort) {
        String host = getLocalHostExactAddress().getHostAddress() + ":"+nettyPort;
        String finalGroup = group+host;
        return finalGroup.replaceAll("\\.", "-").replaceAll(":","-") + group;
    }

    public static InetAddress getLocalHostExactAddress() {
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
