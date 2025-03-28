package com.lrh.message.enums;

import lombok.Getter;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.enums
 * @ClassName: MessageTypeEnum
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 00:36
 */
@Getter
public enum MessageTypeEnum {

    /**
     * 纯消息文本
     */
    TextMessage(0, "text", "纯消息文本"),

    /**
     * 图片消息
     */
    PhotoMessage(1, "photo", "图片消息"),

    /**
     * 错误消息
     */
    ErrorMessage(2, "error", "发送失败"),

    /**
     * ping消息
     */
    PingMessage(4, "ping", "ping消息"),

    /**
     * 不是好友多发消息
     */
    NotFriendMessage(5, "not_friend", "不是好友多发消息"),;


    private final Integer code;

    private final String messageType;

    private final String typeName;

    MessageTypeEnum(Integer code, String messageType, String typeName) {
        this.code = code;
        this.messageType = messageType;
        this.typeName = typeName;
    }

}
