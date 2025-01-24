package com.lrh.message.controller;

import com.lrh.common.result.Result;
import com.lrh.message.dto.req.FriendApplyAddReq;
import com.lrh.message.dto.req.FriendApplyPageReq;
import com.lrh.message.dto.req.FriendApplyUpdateReq;
import com.lrh.message.dto.resp.FriendApplyResp;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.service.FriendApplyService;
import org.springframework.web.bind.annotation.*;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.controller
 * @ClassName: FriendApplyController
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 01:06
 */
@RestController
@RequestMapping("/api/friend-apply")
public class FriendApplyController {

    private final FriendApplyService friendApplyService;


    public FriendApplyController(FriendApplyService friendApplyService) {
        this.friendApplyService = friendApplyService;
    }

    @GetMapping("/page")
    public Result<PageDTO<FriendApplyResp>> getFriendApplyPage(FriendApplyPageReq req){
        PageDTO<FriendApplyResp> pageDTO = friendApplyService.getFriendApplyPage(req);
        return Result.success(pageDTO);
    }

    @PostMapping("/add")
    public Result<Object> addFriendApply(@RequestBody FriendApplyAddReq req){
        friendApplyService.addFriendApply(req);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Object> updateFriendApply(@RequestBody FriendApplyUpdateReq req){
        friendApplyService.updateFriendApply(req);
        return Result.success();
    }

    @GetMapping("/count")
    public Result<Long> getFriendApplyCount(String userId){
        Long count = friendApplyService.getFriendApplyCount(userId);
        return Result.success(count);
    }


}
