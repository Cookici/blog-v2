package com.lrh.blog.user.constant;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.constant
 * @ClassName: RedisKeyConstant
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 21:02
 */

public class RedisKeyConstant {
    public static final String LOGIN_LOCK_KEY = "lock:user:login:%s";
    public static final String REGISTER_LOCK_KEY = "lock:user:register:%s";
    public static final String LOGIN_HASH_KEY = "user:login_hash";
}
