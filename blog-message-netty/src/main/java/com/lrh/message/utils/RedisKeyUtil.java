package com.lrh.message.utils;

import com.lrh.message.constants.RedisKeyConstant;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.utils
 * @ClassName: RedisKeyUtil
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/17 21:17
 */

public class RedisKeyUtil {

    public static String getMessageOneToOneRedisKey(String userId, String toUserId) {
        userId = userId.contains("_") ? userId.split("_", 2)[1] : userId;
        toUserId = toUserId.contains("_") ? toUserId.split("_", 2)[1] : toUserId;
        return RedisKeyConstant.USER_MESSAGE_PREFIX + (userId.compareTo(toUserId) >= 0 ? userId + "-" + toUserId : toUserId + "-" + userId);
    }

}
