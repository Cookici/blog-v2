package com.lrh.message.service.impl;

import com.lrh.message.constants.RedisKeyConstant;
import com.lrh.message.model.MessageModel;
import com.lrh.message.utils.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.service
 * @ClassName: ThreadPoolService
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/14 15:13
 */
@Service
public class ThreadPoolService {
    private final ThreadPoolExecutor threadPoolExecutor;

    private final RedisTemplate<String,Object> redisTemplate;

    public ThreadPoolService(ThreadPoolExecutor threadPoolExecutor, RedisTemplate<String, Object> redisTemplate) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.redisTemplate = redisTemplate;
    }

    public void submitTask(Runnable task) {
        threadPoolExecutor.submit(task);
    }


    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

    public void setNoOnlineMessageCache(MessageModel messageModel){
        submitTask(() -> {
            redisTemplate.opsForZSet().add(RedisKeyConstant.NO_ONLINE_MESSAGE_PREFIX +
                            RedisKeyUtil.getMessageOneToOneRedisKey(messageModel.getUserId(), messageModel.getToUserId()),
                    messageModel, messageModel.getTimestamp());
        });
    }
}
