package com.lrh.blog.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrh.blog.user.dto.cqe.*;
import com.lrh.blog.user.dto.req.ImageUploadReq;
import com.lrh.blog.user.dto.resp.*;
import com.lrh.blog.user.dto.vo.UserVO;
import com.lrh.blog.user.model.UserModel;

import java.util.List;
import java.util.Map;

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

    Map<String, UserVO> getUserByIds(List<String> userIds);

    UserUpdatePasswordResp updateUserPassword(UserUpdatePasswordCmd cmd);

    FileUploadResp uploadAvatar(ImageUploadReq req);

    void logout();

    String parseIp(String ip);

    String updateIp(UserUpdateIpCmd cmd);

    PageDTO<UserVO> searchPage(UserSearchPageCmd cmd);
}
