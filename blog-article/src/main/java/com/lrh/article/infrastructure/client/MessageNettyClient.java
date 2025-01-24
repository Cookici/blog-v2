package com.lrh.article.infrastructure.client;

import com.lrh.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.infrastructure.client
 * @ClassName: MessageNettyClient
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 13:28
 */
@FeignClient(name = "blog-message-netty")
public interface MessageNettyClient {
    @GetMapping("/api/friend-apply/count")
    Result<Long> getFriendApplyCount(@RequestParam("userId") String userId);
}
