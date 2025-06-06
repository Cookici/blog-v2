package com.lrh.message.constants;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.constants
 * @ClassName: RedisKeyConstant
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 14:52
 */

public class RedisKeyConstant {
    public static final String LOGIN_HASH_KEY = "user:login_hash";
    public static final String NETTY_SERVER_SET_KEY = "netty:server_set";
    public static final String USERID_NETTY_HASH_KEY = "user:netty_hash";
    public static final String USER_MESSAGE_PREFIX = "user:chat_";
    public static final String USER_FRIEND_PREFIX = "user:friend_";
    public static final String FRIEND_OPERATOR_LOCK = "lock:user:friend:user_%s_friend_%s";
}
