package com.lrh.message.controller;

import com.lrh.common.result.Result;
import com.lrh.message.dto.req.FriendDeleteReq;
import com.lrh.message.dto.req.FriendPageReq;
import com.lrh.message.dto.req.FriendUpdateNameReq;
import com.lrh.message.dto.resp.FriendResp;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.service.FriendService;
import org.springframework.web.bind.annotation.*;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.controller
 * @ClassName: FriendController
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 16:58
 */
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;


    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/update/name")
    public Result<Object> updateFriendName(@RequestBody FriendUpdateNameReq req) {
        friendService.updateFriendName(req);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageDTO<FriendResp>> getFriendPage(FriendPageReq req) {
        PageDTO<FriendResp> friendPage = friendService.getFriendPage(req);
        return Result.success(friendPage);
    }

    @PostMapping("/delete")
    public Result<Object> deleteFriend(@RequestBody FriendDeleteReq req) {
        friendService.deleteFriend(req);
        return Result.success();
    }

}
