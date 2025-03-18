package com.lrh.blog.user.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrh.blog.user.constant.DESConstant;
import com.lrh.blog.user.constant.RedisKeyConstant;
import com.lrh.blog.user.constant.UserConstant;
import com.lrh.blog.user.dto.cqe.*;
import com.lrh.blog.user.dto.req.ImageUploadReq;
import com.lrh.blog.user.dto.resp.*;
import com.lrh.blog.user.dto.vo.UserVO;
import com.lrh.blog.user.event.UserUpdateEvent;
import com.lrh.blog.user.mapper.UserMapper;
import com.lrh.blog.user.model.UserModel;
import com.lrh.blog.user.romote.OssClient;
import com.lrh.blog.user.romote.RoleClient;
import com.lrh.blog.user.romote.dto.req.UserRoleBindReq;
import com.lrh.blog.user.service.UserService;
import com.lrh.blog.user.util.DESUtil;
import com.lrh.blog.user.util.LockUtil;
import com.lrh.common.annotations.ExecutionRecords;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.result.Result;
import com.lrh.common.util.HostUtil;
import com.lrh.common.util.IdUtil;
import com.lrh.common.util.JwtUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Value("${ip-key}")
    private String ipKey;

    @Value("${ip-url}")
    private String ipUrl;

    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedissonClient redissonClient;

    private final OssClient ossClient;

    private final RoleClient roleClient;

    private final ApplicationEventPublisher eventPublisher;
    
    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate, 
                          RedissonClient redissonClient, OssClient ossClient, 
                          RoleClient roleClient, ApplicationEventPublisher eventPublisher) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
        this.ossClient = ossClient;
        this.roleClient = roleClient;
        this.eventPublisher = eventPublisher;
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
                        token = getToken(resp.getUserId(), resp.getUserName());
                        redisTemplate.opsForHash().put(RedisKeyConstant.LOGIN_HASH_KEY, userModel.getUserId(), token);
                        redisTemplate.expire(RedisKeyConstant.LOGIN_HASH_KEY, 2, TimeUnit.HOURS);
                    }
                }
        );
    }


    private String getToken(String userId, String userName) {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("userName", userName);
        return JwtUtil.getToken(payload);
    }


    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
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
            int insert = userMapper.insert(userModel);
            if (insert <= 0) {
                return null;
            }

            Result<Boolean> booleanResult = roleClient
                    .bindUserRole(new UserRoleBindReq(userModel.getUserId(),UserConstant.LOGIN_ROLE));
            if (!booleanResult.getData()) {
                throw new RuntimeException("角色绑定失败");
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
        
        // 发布用户更新事件
        eventPublisher.publishEvent(new UserUpdateEvent(this, cmd.getUserId(), cmd.getUserName()));
        log.info("[UserServiceImpl] updateUserInfo 用户信息更新成功，已发布更新事件: userId={}, userName={}", cmd.getUserId(), cmd.getUserName());
        
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

    @Override
    public String updateIp(UserUpdateIpCmd req) {
        LambdaUpdateWrapper<UserModel> updateWrapper = Wrappers.lambdaUpdate(UserModel.class)
                .eq(UserModel::getUserId, UserContext.getUserId())
                .eq(UserModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .set(UserModel::getUserIp, req.getUserIp());
        userMapper.update(updateWrapper);
        return req.getUserIp();
    }

    @Override
    public PageDTO<UserVO> searchPage(UserSearchPageCmd cmd) {

        LambdaQueryWrapper<UserModel> queryWrapper = Wrappers.lambdaQuery(UserModel.class)
                .eq(UserModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .and(wrapper ->
                        wrapper.like(UserModel::getUserName, cmd.getKeyword())
                                .or()
                                .like(UserModel::getUserPhone, cmd.getKeyword())
                );

        Long total = userMapper.selectCount(queryWrapper);
        if(total == null || total == 0L){
            return new PageDTO<>();
        }

        queryWrapper.last("limit " + (cmd.getPageNum() - 1) * cmd.getPageSize() + "," + cmd.getPageSize());
        List<UserModel> userModels = userMapper.selectList(queryWrapper);
        List<UserVO> userVOList = userModels.stream().map(userModel -> new UserVO(userModel.getUserId(),
                userModel.getUserName(),
                userModel.getUserPhoto(),
                userModel.getUserLevel())).collect(Collectors.toList());

        return PageDTO.<UserVO>builder()
                .total(total)
                .page(cmd.getPageNum())
                .pageSize(cmd.getPageSize())
                .data(userVOList)
                .build();
    }

    @Override
    public String parseIp(String ip) {
        Boolean isIp = HostUtil.judgeIp(ip);
        if (!isIp) {
            return "unknown";
        }
        return doParseIp(ip);
    }

    private String doParseIp(String ip) {
        String apiUrl = ipUrl + "?key=" + ipKey + "&ip=" + ip + "&coordsys=WGS84";
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String line = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            connection =
                    (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            reader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream(), StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.info("[UserServiceImpl] doParseIp 解析失败");
            return "unknown";
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                log.info("[UserServiceImpl] doParseIp 关闭失败");
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        JSONObject jsonObject = JSON.parseObject(result.toString());
        JSONObject data = jsonObject.getJSONObject("data");
        return Objects.equals(data.getString("prov"), "") ? "unknown" : data.getString("prov");
    }

    @Override
    public List<String> getActiveUserIds(int limit) {
        // 基于最近登录时间获取活跃用户
        LambdaQueryWrapper<UserModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UserModel::getUpdateTime)
                    .last("LIMIT " + limit);
        
        List<UserModel> activeUsers = this.list(queryWrapper);
        
        return activeUsers.stream()
                .map(UserModel::getUserId)
                .collect(Collectors.toList());
    }

}
