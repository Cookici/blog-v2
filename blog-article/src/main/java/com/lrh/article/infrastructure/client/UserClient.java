package com.lrh.article.infrastructure.client;

import com.lrh.article.domain.vo.UserVO;
import com.lrh.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.client
 * @ClassName: UserClient
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:58
 */

@FeignClient(name = "blog-user")
public interface UserClient {
    @GetMapping("/api/user/getByIds")
    Result<Map<String, UserVO>> getByIds(@RequestParam("userIds") List<String> userIds);
    
    /**
     * 获取活跃用户ID列表
     * @param limit 限制返回数量
     * @return 活跃用户ID列表
     */
    @GetMapping("/api/user/active")
    Result<List<String>> getActiveUserIds(@RequestParam("limit") Integer limit);
}


