package com.lrh.blog.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrh.blog.user.dao.UserModel;
import com.lrh.blog.user.dto.cqe.UserLoginQuery;
import com.lrh.blog.user.dto.cqe.UserRegisterCmd;
import com.lrh.blog.user.dto.cqe.UserUpdateCmd;
import com.lrh.blog.user.dto.resp.UserLoginResp;
import com.lrh.blog.user.dto.resp.UserRegisterResp;
import com.lrh.blog.user.dto.resp.UserUpdateResp;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.service
 * @ClassName: UserService
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午2:09
 */

public interface UserService extends IService<UserModel> {

    UserLoginResp login(UserLoginQuery query);

    UserRegisterResp register(UserRegisterCmd cmd);

    UserUpdateResp updateUserInfo(UserUpdateCmd cmd);
}
