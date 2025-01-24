package com.lrh.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.result.Result;
import com.lrh.common.util.IdUtil;
import com.lrh.message.client.UserClient;
import com.lrh.message.client.UserVO;
import com.lrh.message.constants.FriendApplyConstant;
import com.lrh.message.dto.req.FriendApplyAddReq;
import com.lrh.message.dto.req.FriendApplyPageReq;
import com.lrh.message.dto.req.FriendApplyUpdateReq;
import com.lrh.message.dto.resp.FriendApplyResp;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.mapper.FriendApplyMapper;
import com.lrh.message.model.FriendApplyModel;
import com.lrh.message.service.FriendApplyService;
import com.lrh.message.service.FriendService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.service.impl
 * @ClassName: FriendApplyServiceImpl
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 01:07
 */
@Service
public class FriendApplyServiceImpl extends ServiceImpl<FriendApplyMapper, FriendApplyModel> implements FriendApplyService {

    private final FriendApplyMapper friendApplyMapper;

    private final FriendService friendService;

    private final UserClient userClient;

    public FriendApplyServiceImpl(FriendApplyMapper friendApplyMapper, FriendService friendService, UserClient userClient) {
        this.friendApplyMapper = friendApplyMapper;
        this.friendService = friendService;
        this.userClient = userClient;
    }

    @Override
    public void addFriendApply(FriendApplyAddReq req) {
        if (!req.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("非法操作");
        }
        FriendApplyModel friendApplyModel = new FriendApplyModel();
        friendApplyModel.setRecordId("apply_record_" + IdUtil.getUuid());
        friendApplyModel.setUserId(req.getUserId());
        friendApplyModel.setAppliedId(req.getAppliedId());
        friendApplyModel.setDescription(req.getDescription());
        friendApplyModel.setApplyStatus(FriendApplyConstant.NO_HANDLER);
        friendApplyMapper.insert(friendApplyModel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFriendApply(FriendApplyUpdateReq req) {
        if (!req.getAppliedId().equals(UserContext.getUserId())) {
            throw new RuntimeException("非法操作");
        }
        if (!Objects.equals(req.getApplyStatus(), FriendApplyConstant.AGREE) &&
                !Objects.equals(req.getApplyStatus(), FriendApplyConstant.NO_AGREE)) {
            throw new RuntimeException("非法操作");
        }
        LambdaUpdateWrapper<FriendApplyModel> wrapper = Wrappers.lambdaUpdate(FriendApplyModel.class)
                .eq(FriendApplyModel::getUserId, req.getUserId())
                .eq(FriendApplyModel::getAppliedId, req.getAppliedId())
                .eq(FriendApplyModel::getApplyStatus, FriendApplyConstant.NO_HANDLER)
                .eq(FriendApplyModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(FriendApplyModel::getApplyStatus, req.getApplyStatus());
        friendApplyMapper.update(wrapper);

        if (Objects.equals(req.getApplyStatus(), FriendApplyConstant.AGREE)) {
            friendService.addFriend(req.getUserId(), req.getAppliedId());
        }
    }

    @Override
    public PageDTO<FriendApplyResp> getFriendApplyPage(FriendApplyPageReq req) {
        if (!Objects.equals(req.getUserId(), UserContext.getUserId())) {
            throw new RuntimeException("非法操作");
        }
        if (req.getPageNum() < 1) {
            req.setPageNum(1L);
        }
        if (req.getPageSize() < 1) {
            req.setPageSize(10L);
        }

        Long total = getFriendApplyCount(req);
        if (total == null || total == 0) {
            return new PageDTO<>();
        }

        List<FriendApplyModel> friendApplyModelList = getFriendApplyModelPage(req);
        List<FriendApplyResp> friendApplyRespList = getFriendApplyRespList(friendApplyModelList);

        return PageDTO.<FriendApplyResp>builder()
                .total(total)
                .data(friendApplyRespList)
                .page(req.getPageNum())
                .pageSize(req.getPageSize())
                .build();
    }

    @Override
    public Long getFriendApplyCount(String userId) {
        if (!Objects.equals(userId, UserContext.getUserId())) {
            throw new RuntimeException("非法操作");
        }
        LambdaQueryWrapper<FriendApplyModel> queryWrapper = Wrappers.lambdaQuery(FriendApplyModel.class)
                .eq(FriendApplyModel::getAppliedId, userId)
                .eq(FriendApplyModel::getApplyStatus, FriendApplyConstant.NO_HANDLER)
                .eq(FriendApplyModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        Long count = friendApplyMapper.selectCount(queryWrapper);
        return count == null ? 0L : count;
    }

    private List<FriendApplyResp> getFriendApplyRespList(List<FriendApplyModel> friendApplyModelList) {
        List<String> userIdList = friendApplyModelList.stream().map(FriendApplyModel::getUserId).collect(Collectors.toList());
        Result<Map<String, UserVO>> clientResult = userClient.getByIds(userIdList);
        Map<String, UserVO> clientResultData = clientResult.getData();
        List<FriendApplyResp> friendApplyRespList = friendApplyModelList.stream().map(friendApplyModel -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setUserInfo(clientResultData.getOrDefault(friendApplyModel.getUserId(), new UserVO()));
            friendApplyResp.setDescription(friendApplyModel.getDescription());
            return friendApplyResp;
        }).collect(Collectors.toList());
        return friendApplyRespList;
    }

    private List<FriendApplyModel> getFriendApplyModelPage(FriendApplyPageReq req) {
        LambdaUpdateWrapper<FriendApplyModel> wrapper = Wrappers.lambdaUpdate(FriendApplyModel.class)
                .eq(FriendApplyModel::getAppliedId, req.getUserId())
                .eq(FriendApplyModel::getApplyStatus, FriendApplyConstant.NO_HANDLER)
                .eq(FriendApplyModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .last("limit " + req.getOffset() + "," + req.getLimit());
        return friendApplyMapper.selectList(wrapper);
    }

    private Long getFriendApplyCount(FriendApplyPageReq req) {
        LambdaUpdateWrapper<FriendApplyModel> wrapper = Wrappers.lambdaUpdate(FriendApplyModel.class)
                .eq(FriendApplyModel::getAppliedId, req.getUserId())
                .eq(FriendApplyModel::getApplyStatus, FriendApplyConstant.NO_HANDLER)
                .eq(FriendApplyModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return friendApplyMapper.selectCount(wrapper);
    }
}
