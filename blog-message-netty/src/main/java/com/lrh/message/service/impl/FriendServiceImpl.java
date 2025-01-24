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
import com.lrh.message.constants.RedisKeyConstant;
import com.lrh.message.dto.req.FriendDeleteReq;
import com.lrh.message.dto.req.FriendPageReq;
import com.lrh.message.dto.req.FriendUpdateNameReq;
import com.lrh.message.dto.resp.FriendResp;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.mapper.FriendMapper;
import com.lrh.message.model.FriendModel;
import com.lrh.message.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.service.impl
 * @ClassName: FriendServiceImpl
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 17:01
 */
@Slf4j
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, FriendModel> implements FriendService {

    private final FriendMapper friendMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserClient userClient;

    public FriendServiceImpl(FriendMapper friendMapper, RedisTemplate<String, Object> redisTemplate, UserClient userClient) {
        this.friendMapper = friendMapper;
        this.redisTemplate = redisTemplate;
        this.userClient = userClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFriend(String userId, String friendId) {
        FriendModel friendUserModel = new FriendModel();
        friendUserModel.setRecordId("friend_record_" + IdUtil.getUuid());
        friendUserModel.setUserId(userId);
        friendUserModel.setFriendId(friendId);

        FriendModel friendModel = new FriendModel();
        friendModel.setRecordId("friend_record_" + IdUtil.getUuid());
        friendModel.setUserId(friendId);
        friendModel.setFriendId(userId);

        friendMapper.insert(friendUserModel);
        friendMapper.insert(friendModel);

        try {
            String script =
                    "local key1 = KEYS[1] " +
                            "local key2 = KEYS[2] " +
                            "local value1 = ARGV[1] " +
                            "local value2 = ARGV[2] " +
                            "local result1 = redis.call('sadd', key1, value1) " +
                            "local result2 = redis.call('sadd', key2, value2) " +
                            "if result1 == 1 and result2 == 1 then " +
                            "    return 1 " +
                            "else " +
                            "    if result1 == 1 then " +
                            "        redis.call('srem', key1, value1) " +
                            "    end " +
                            "    if result2 == 1 then " +
                            "        redis.call('srem', key2, value2) " +
                            "    end " +
                            "    return 0 " +
                            "end";
            String key1 = RedisKeyConstant.USER_FRIEND_PREFIX + userId;
            String key2 = RedisKeyConstant.USER_FRIEND_PREFIX + friendId;
            List<String> keys = Arrays.asList(key1, key2);

            Long result = redisTemplate.execute(
                    new DefaultRedisScript<>(script, Long.class),
                    keys,
                    friendId,
                    userId
            );

            if (result != 1) {
                throw new RuntimeException("Redis添加好友失败");
            }
        } catch (Exception e) {
            log.error("Redis添加好友失败: {}", e.getMessage(), e);
            throw new RuntimeException("Redis添加好友失败: " + e.getMessage());
        }
    }

    @Override
    public void updateFriendName(FriendUpdateNameReq req) {
        if (!req.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("非法请求");
        }
        LambdaUpdateWrapper<FriendModel> updateWrapper = Wrappers.lambdaUpdate(FriendModel.class)
                .eq(FriendModel::getUserId, req.getUserId())
                .eq(FriendModel::getFriendId, req.getFriendId())
                .eq(FriendModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(FriendModel::getFriendName, req.getFriendName());
        friendMapper.update(updateWrapper);
    }

    @Override
    public PageDTO<FriendResp> getFriendPage(FriendPageReq req) {
        if (!req.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("非法请求");
        }
        if (req.getPageNum() < 1) {
            req.setPageNum(1L);
        }
        if (req.getPageSize() < 1) {
            req.setPageSize(10L);
        }

        Long total = getFriendPageCount(req);
        if (total == null || total == 0L) {
            return new PageDTO<>();
        }
        List<FriendModel> friendModelList = getFriendModelPage(req);
        List<FriendResp> friendRespList = getFriendRespList(friendModelList);

        return PageDTO.<FriendResp>builder()
                .page(req.getPageNum())
                .pageSize(req.getPageSize())
                .total(total)
                .data(friendRespList)
                .build();
    }

    private List<FriendResp> getFriendRespList(List<FriendModel> friendModelList) {
        List<String> userIdList = friendModelList.stream().map(FriendModel::getFriendId).collect(Collectors.toList());
        Result<Map<String, UserVO>> clientMessage = userClient.getByIds(userIdList);
        Map<String, UserVO> clientData = clientMessage.getData();
        List<FriendResp> respList = new ArrayList<>();
        friendModelList.forEach(friendModel -> {
            FriendResp friendResp = new FriendResp();
            friendResp.setFriendName(friendModel.getFriendName());
            friendResp.setUserInfo(clientData.getOrDefault(friendModel.getFriendId(), new UserVO()));
            respList.add(friendResp);
        });
        return respList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(FriendDeleteReq req) {
        if (!req.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("非法请求");
        }

        LambdaUpdateWrapper<FriendModel> updateWrapper = Wrappers.lambdaUpdate(FriendModel.class)
                .eq(FriendModel::getUserId, req.getUserId())
                .eq(FriendModel::getFriendId, req.getFriendId())
                .eq(FriendModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(FriendModel::getIsDeleted, BusinessConstant.IS_DELETED);
        friendMapper.update(updateWrapper);

        updateWrapper.clear();
        updateWrapper.eq(FriendModel::getUserId, req.getFriendId())
                .eq(FriendModel::getFriendId, req.getUserId())
                .eq(FriendModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(FriendModel::getIsDeleted, BusinessConstant.IS_DELETED);
        friendMapper.update(updateWrapper);

        try {
            String script =
                    "local key1 = KEYS[1] " +
                            "local key2 = KEYS[2] " +
                            "local value1 = ARGV[1] " +
                            "local value2 = ARGV[2] " +
                            "local result1 = redis.call('srem', key1, value1) " +
                            "local result2 = redis.call('srem', key2, value2) " +
                            "if result1 == 1 and result2 == 1 then " +
                            "    return 1 " +
                            "else " +
                            "    if result1 == 1 then " +
                            "        redis.call('sadd', key1, value1) " +
                            "    end " +
                            "    if result2 == 1 then " +
                            "        redis.call('sadd', key2, value2) " +
                            "    end " +
                            "    return 0 " +
                            "end";

            String key1 = RedisKeyConstant.USER_FRIEND_PREFIX + req.getUserId();
            String key2 = RedisKeyConstant.USER_FRIEND_PREFIX + req.getFriendId();
            List<String> keys = Arrays.asList(key1, key2);

            Long result = redisTemplate.execute(
                    new DefaultRedisScript<>(script, Long.class),
                    keys,
                    req.getFriendId(),
                    req.getUserId()
            );

            if (result != 1) {
                log.error("Redis删除好友失败 - user1: {}, user2: {}", req.getUserId(), req.getFriendId());
                throw new RuntimeException("Redis删除好友失败");
            }

            log.debug("成功删除好友关系 - user1: {}, user2: {}", req.getUserId(), req.getFriendId());
        } catch (Exception e) {
            log.error("Redis删除好友操作异常: {}", e.getMessage(), e);
            throw new RuntimeException("删除好友失败: " + e.getMessage());
        }
    }

    @Override
    public Boolean selectIsFirend(String userId, String toUserId) {
        try {
            // 修改后的 Lua 脚本
            String script =
                    "local key1 = KEYS[1] " +
                            "local key2 = KEYS[2] " +
                            "local value1 = ARGV[1] " +
                            "local value2 = ARGV[2] " +
                            "local isFriend = redis.call('sismember', key1, value1) " +
                            "local isToFriend = redis.call('sismember', key2, value2) " +
                            "if isFriend == 1 and isToFriend == 1 then " +
                            "    return 1 " +
                            "else " +
                            "    return 0 " +
                            "end";

            // 准备 Redis keys
            String key1 = RedisKeyConstant.USER_FRIEND_PREFIX + userId;
            String key2 = RedisKeyConstant.USER_FRIEND_PREFIX + toUserId;
            List<String> keys = Arrays.asList(key1, key2);

            // 执行 Lua 脚本
            Long result = redisTemplate.execute(
                    new DefaultRedisScript<>(script, Long.class),
                    keys,
                    toUserId,
                    userId
            );

            log.debug("检查好友关系 - user1: {}, user2: {}, result: {}", userId, toUserId, result);
            return result == 1;
        } catch (Exception e) {
            log.error("检查好友关系失败: {}", e.getMessage(), e);
            return checkFriendInDatabase(userId, toUserId);
        }
    }

    private Boolean checkFriendInDatabase(String userId, String toUserId) {
        try {
            LambdaQueryWrapper<FriendModel> queryWrapper = Wrappers.lambdaQuery(FriendModel.class)
                    .eq(FriendModel::getUserId, userId)
                    .eq(FriendModel::getFriendId, toUserId)
                    .eq(FriendModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);

            LambdaQueryWrapper<FriendModel> reverseWrapper = Wrappers.lambdaQuery(FriendModel.class)
                    .eq(FriendModel::getUserId, toUserId)
                    .eq(FriendModel::getFriendId, userId)
                    .eq(FriendModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);

            return friendMapper.exists(queryWrapper) && friendMapper.exists(reverseWrapper);
        } catch (Exception e) {
            log.error("数据库查询好友关系失败: {}", e.getMessage(), e);
            return false;
        }
    }


    private Long getFriendPageCount(FriendPageReq req) {
        LambdaQueryWrapper<FriendModel> queryWrapper = Wrappers.lambdaQuery(FriendModel.class)
                .eq(FriendModel::getUserId, req.getUserId())
                .eq(FriendModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return friendMapper.selectCount(queryWrapper);
    }

    private List<FriendModel> getFriendModelPage(FriendPageReq req) {
        LambdaQueryWrapper<FriendModel> queryWrapper = Wrappers.lambdaQuery(FriendModel.class)
                .eq(FriendModel::getUserId, req.getUserId())
                .eq(FriendModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .last("limit " + req.getOffset() + "," + req.getLimit());
        return friendMapper.selectList(queryWrapper);
    }
}
