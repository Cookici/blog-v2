package com.lrh.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrh.message.dto.req.FriendDeleteReq;
import com.lrh.message.dto.req.FriendPageReq;
import com.lrh.message.dto.req.FriendUpdateNameReq;
import com.lrh.message.dto.resp.FriendResp;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.model.FriendModel;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.service
 * @ClassName: FriendService
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 17:01
 */

public interface FriendService extends IService<FriendModel> {

    void updateFriendName(FriendUpdateNameReq req);

    PageDTO<FriendResp> getFriendPage(FriendPageReq req);

    void deleteFriend(FriendDeleteReq req);

    Boolean selectIsFirend(String userId, String toUserId);

    void addFriend(String userId, String friendId);
}
