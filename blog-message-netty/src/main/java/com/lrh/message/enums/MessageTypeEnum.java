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
     * 注册消息
     */
    RegisterMessage(3, "register", "注册消息"),

    /**
     * 远程错误消息
     */
    RemoteErrorMessage(4, "remoteError", "远程错误消息");


    private final Integer code;

    private final String messageType;

    private final String typeName;

    MessageTypeEnum(Integer code, String messageType, String typeName) {
        this.code = code;
        this.messageType = messageType;
        this.typeName = typeName;
    }

}
