package com.lrh.message.netty;

import io.netty.util.AttributeKey;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.netty
 * @ClassName: Attributes
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/12 19:15
 */
public interface Attributes {
    AttributeKey<String> USERID =  AttributeKey.newInstance("userId");
}
