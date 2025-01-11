package com.lrh.common.util;

import com.lrh.common.context.UserContext;

import java.util.UUID;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.common.util
 * @ClassName: UserIdUtil
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/9 19:09
 */

public class IdUtil {
    public static String getUserId(String userId) {
        return userId == null || userId.isEmpty() ? UserContext.getUserId() : userId;
    }

    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String getMessageOneToOneRedisKey(String userId, String toUserId) {
        userId = userId.contains("_") ? userId.split("_", 2)[1] : userId;
        toUserId = toUserId.contains("_") ? toUserId.split("_", 2)[1] : toUserId;
        return userId.compareTo(toUserId) >= 0 ? userId + "-" + toUserId : toUserId + "-" + userId;
    }

}
