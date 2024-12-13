package com.lrh.blog.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrh.blog.user.constant.DESConstant;
import com.lrh.blog.user.constant.UserConstant;
import com.lrh.blog.user.dao.UserModel;
import com.lrh.blog.user.dto.cqe.UserLoginQuery;
import com.lrh.blog.user.dto.cqe.UserRegisterCmd;
import com.lrh.blog.user.dto.cqe.UserUpdateCmd;
import com.lrh.blog.user.dto.resp.UserLoginResp;
import com.lrh.blog.user.dto.resp.UserRegisterResp;
import com.lrh.blog.user.dto.resp.UserUpdateResp;
import com.lrh.blog.user.mapper.UserMapper;
import com.lrh.blog.user.service.UserService;
import com.lrh.blog.user.util.DESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserLoginResp login(UserLoginQuery query) {
        LambdaQueryWrapper<UserModel> queryWrapper = Wrappers.lambdaQuery(UserModel.class)
                .eq(UserModel::getUserName, query.getUserName())
                .eq(UserModel::getIsDeleted, UserConstant.IS_NOT_DELETED);
        UserModel userModel = userMapper.selectOne(queryWrapper);
        try {
            String password = DESUtil.decrypt(userModel.getUserPassword(), DESConstant.PASSWORD_KEY);
            if (!password.equals(query.getUserPassword())) {
                return null;
            }
        } catch (Exception e) {
            log.error("[UserServiceImpl] login error : {}", query.getUserPassword());
            return null;
        }
        UserLoginResp resp = new UserLoginResp().convertedUserModelToUserLoginResp(userModel);
        redisTemplate.opsForValue().setIfAbsent(resp.getUserId(), resp.getToken(), 2, TimeUnit.HOURS);
        return resp;
    }


    @Override
    public UserRegisterResp register(UserRegisterCmd cmd) {
        UserModel userModel = new UserModel();
        userModel.setUserId(UUID.randomUUID().toString());
        userModel.setUserName(cmd.getUserName());
        userModel.setUserPassword(cmd.getUserPassword());
        userModel.setUserPhone(cmd.getUserPhone());
        userModel.setUserBirthday(cmd.getUserBirthday());
        userModel.setUserSex(cmd.getUserSex());
        userModel.setUserLevel(cmd.getUserLevel());
        userModel.setUserIp(cmd.getUserIp());
        userModel.setUserEmail(cmd.getUserEmail());
        userModel.setRoleName(cmd.getUserRole());
        int insert = userMapper.insert(userModel);
        if (insert <= 0) {
            return null;
        }
        return new UserRegisterResp().convertedUserModelToUserLoginResp(userModel);
    }

    @Override
    public UserUpdateResp updateUserInfo(UserUpdateCmd cmd) {
        LambdaUpdateWrapper<UserModel> updateWrapper = Wrappers.lambdaUpdate(UserModel.class)
                .eq(UserModel::getUserId, cmd.getUserId())
                .eq(UserModel::getIsDeleted, UserConstant.IS_NOT_DELETED)
                .set(UserModel::getUserName, cmd.getUserName())
                .set(UserModel::getUserPassword, cmd.getUserPassword())
                .set(UserModel::getUserPhone, cmd.getUserPhone())
                .set(UserModel::getUserLevel, cmd.getUserLevel())
                .set(UserModel::getUserSex, cmd.getUserSex())
                .set(UserModel::getUserBirthday, cmd.getUserBirthday())
                .set(UserModel::getUserIp, cmd.getUserIp())
                .set(UserModel::getUserEmail, cmd.getUserEmail())
                .set(UserModel::getRoleName, cmd.getRoleName());
        int update = userMapper.update(updateWrapper);
        if (update <= 0) {
            return null;
        }
        return new UserUpdateResp(update);
    }
}
