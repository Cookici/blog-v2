package com.lrh.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrh.message.dto.req.FriendApplyAddReq;
import com.lrh.message.dto.req.FriendApplyPageReq;
import com.lrh.message.dto.req.FriendApplyUpdateReq;
import com.lrh.message.dto.resp.FriendApplyResp;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.model.FriendApplyModel;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.service
 * @ClassName: FriendApplyService
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 01:07
 */

public interface FriendApplyService extends IService<FriendApplyModel> {

    void addFriendApply(FriendApplyAddReq req);

    void updateFriendApply(FriendApplyUpdateReq req);

    PageDTO<FriendApplyResp> getFriendApplyPage(FriendApplyPageReq req);

    Long getFriendApplyCount(String userId);
}
