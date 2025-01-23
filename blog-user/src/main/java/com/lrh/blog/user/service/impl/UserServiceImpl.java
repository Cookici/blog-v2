package com.lrh.blog.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrh.blog.user.constant.DESConstant;
import com.lrh.blog.user.constant.RedisKeyConstant;
import com.lrh.blog.user.constant.UserConstant;
import com.lrh.blog.user.dto.cqe.UserLoginQuery;
import com.lrh.blog.user.dto.cqe.UserRegisterCmd;
import com.lrh.blog.user.dto.cqe.UserUpdateCmd;
import com.lrh.blog.user.dto.cqe.UserUpdatePasswordCmd;
import com.lrh.blog.user.dto.req.ImageUploadReq;
import com.lrh.blog.user.dto.resp.*;
import com.lrh.blog.user.dto.vo.UserVO;
import com.lrh.blog.user.mapper.UserMapper;
import com.lrh.blog.user.model.UserModel;
import com.lrh.blog.user.romote.OssClient;
import com.lrh.blog.user.service.UserService;
import com.lrh.blog.user.util.DESUtil;
import com.lrh.blog.user.util.LockUtil;
import com.lrh.common.annotations.ExecutionRecords;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.result.Result;
import com.lrh.common.util.IdUtil;
import com.lrh.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.service.impl
 * @ClassName: UserServiceImpl
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午2:10
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserModel> implements UserService {

    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedissonClient redissonClient;

    private final OssClient ossClient;

    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient, OssClient ossClient) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
        this.ossClient = ossClient;
    }

    @Override
    @ExecutionRecords(key = "user-login", userLabel = "#query.userPhone", maxTimes = 5, cooldown = 30, timeUnit = TimeUnit.MINUTES)
    public UserLoginResp login(UserLoginQuery query) {
        LambdaQueryWrapper<UserModel> queryWrapper = Wrappers.lambdaQuery(UserModel.class)
                .eq(UserModel::getUserPhone, query.getUserPhone())
                .eq(UserModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        UserModel userModel = userMapper.selectOne(queryWrapper);
        try {
            String password = DESUtil.decrypt(userModel.getUserPassword(), DESConstant.PASSWORD_KEY);
            if (!password.equals(query.getUserPassword())) {
                throw new RuntimeException(BusinessConstant.LOGIN_FAIL_RECORD);
            }
        } catch (Exception e) {
            log.error("[UserServiceImpl] login error : {}", query.getUserPassword());
            throw new RuntimeException("Login failed");
        }
        UserLoginResp resp = new UserLoginResp().convertedUserModelToUserLoginResp(userModel);
        fetchOrGenerateToken(resp.getUserId(), resp, userModel);
        return resp;
    }

    private void fetchOrGenerateToken(String redisKey, UserLoginResp resp, UserModel userModel) {
        LockUtil lockUtil = new LockUtil(redissonClient);
        lockUtil.executeWithLock(
                String.format(RedisKeyConstant.LOGIN_LOCK_KEY, redisKey), 4, TimeUnit.SECONDS,
                () -> {
                    String token = (String) redisTemplate.opsForHash().get(RedisKeyConstant.LOGIN_HASH_KEY, userModel.getUserId());
                    if (token == null) {
                        token = getToken(resp.getUserId(), resp.getUserName(), userModel.getRoleName());
                        redisTemplate.opsForHash().put(RedisKeyConstant.LOGIN_HASH_KEY, userModel.getUserId(), token);
                        redisTemplate.expire(RedisKeyConstant.LOGIN_HASH_KEY, 2, TimeUnit.HOURS);
                    }
                }
        );
    }


    private String getToken(String userId, String userName, String roleName) {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("userName", userName);
        payload.put("roleName", roleName);
        return JwtUtil.getToken(payload);
    }


    @Override
    public UserRegisterResp register(UserRegisterCmd cmd) {
        LockUtil lockUtil = new LockUtil(redissonClient);
        return lockUtil.tryLock(String.format(RedisKeyConstant.REGISTER_LOCK_KEY, cmd.getUserPhone()), () -> {
            UserModel userModel = new UserModel();
            userModel.setUserId("user_" + IdUtil.getUuid());
            userModel.setUserName(cmd.getUserName());
            userModel.setUserPassword(cmd.getUserPassword());
            userModel.setUserPhone(cmd.getUserPhone());
            userModel.setUserBirthday(cmd.getUserBirthday());
            userModel.setUserSex(cmd.getUserSex());
            userModel.setUserLevel(UserConstant.DEFAULT_LEVEL);
            userModel.setUserIp(cmd.getUserIp());
            userModel.setUserEmail(cmd.getUserEmail());
            userModel.setRoleName(UserConstant.LOGIN_ROLE);
            int insert = userMapper.insert(userModel);
            if (insert <= 0) {
                return null;
            }
            return new UserRegisterResp().convertedUserModelToUserLoginResp(userModel);
        });
    }

    @Override
    public UserUpdateResp updateUserInfo(UserUpdateCmd cmd) {
        LambdaUpdateWrapper<UserModel> updateWrapper = Wrappers.lambdaUpdate(UserModel.class)
                .eq(UserModel::getUserId, cmd.getUserId())
                .eq(UserModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(UserModel::getUserName, cmd.getUserName())
                .set(UserModel::getUserSex, cmd.getUserSex())
                .set(UserModel::getUserBirthday, cmd.getUserBirthday());
        int update = userMapper.update(updateWrapper);
        if (update <= 0) {
            return null;
        }
        return new UserUpdateResp(update);
    }

    @Override
    public Map<String, UserVO> getUserByIds(List<String> userIds) {
        LambdaQueryWrapper<UserModel> queryWrapper = Wrappers.lambdaQuery(UserModel.class)
                .in(UserModel::getUserId, userIds)
                .eq(UserModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);

        List<UserModel> userModelList = userMapper.selectList(queryWrapper);

        return userModelList.stream().collect(Collectors.toMap(
                UserModel::getUserId,
                userModel -> new UserVO(userModel.getUserId(),
                        userModel.getUserName(),
                        userModel.getUserPhoto(),
                        userModel.getUserLevel())
        ));
    }

    @Override
    public UserUpdatePasswordResp updateUserPassword(UserUpdatePasswordCmd cmd) {
        LambdaUpdateWrapper<UserModel> updateWrapper = Wrappers.lambdaUpdate(UserModel.class)
                .eq(UserModel::getUserId, cmd.getUserId())
                .eq(UserModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .eq(UserModel::getUserPassword, cmd.getUserPassword())
                .set(UserModel::getUserPassword, cmd.getNewUserPassword());
        int update = userMapper.update(updateWrapper);
        if (update <= 0) {
            return null;
        }
        return new UserUpdatePasswordResp(update);
    }

    @Override
    public FileUploadResp uploadAvatar(ImageUploadReq req) {
        Result<FileUploadResp> upload = ossClient.upload(req);
        FileUploadResp data = upload.getData();
        String fileUrl = data.getFileUrl();
        LambdaUpdateWrapper<UserModel> updateWrapper = Wrappers.lambdaUpdate(UserModel.class)
                .eq(UserModel::getUserId, UserContext.getUserId())
                .eq(UserModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(UserModel::getUserPhoto, fileUrl);
        userMapper.update(updateWrapper);
        return data;
    }

    @Override
    public void logout() {
        if (UserContext.getUserId() == null || UserContext.getUserId().isEmpty()) {
            return;
        }
        if (redisTemplate.hasKey(RedisKeyConstant.LOGIN_HASH_KEY)) {
            redisTemplate.opsForHash().delete(RedisKeyConstant.LOGIN_HASH_KEY, UserContext.getUserId());
        }
    }

}
