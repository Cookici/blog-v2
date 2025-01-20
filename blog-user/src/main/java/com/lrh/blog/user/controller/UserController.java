package com.lrh.blog.user.controller;

import com.lrh.blog.user.dto.cqe.UserLoginQuery;
import com.lrh.blog.user.dto.cqe.UserRegisterCmd;
import com.lrh.blog.user.dto.cqe.UserUpdateCmd;
import com.lrh.blog.user.dto.cqe.UserUpdatePasswordCmd;
import com.lrh.blog.user.dto.req.*;
import com.lrh.blog.user.dto.resp.*;
import com.lrh.blog.user.dto.vo.UserVO;
import com.lrh.blog.user.service.UserService;
import com.lrh.common.exception.NoUserException;
import com.lrh.common.result.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.controller
 * @ClassName: UserController
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午2:02
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<UserLoginResp> login(@RequestBody UserLoginReq req) {
        UserLoginQuery query = new UserLoginQuery(req);
        UserLoginResp resp = userService.login(query);
        if (resp == null) {
            throw new NoUserException();
        }
        return Result.success(resp);
    }

    @PostMapping("/register")
    public Result<UserRegisterResp> register(@RequestBody UserRegisterReq req, HttpServletRequest httpServletRequest) throws Exception {
        UserRegisterCmd cmd = new UserRegisterCmd(req, httpServletRequest);
        UserRegisterResp resp = userService.register(cmd);
        if (resp == null) {
            return Result.fail();
        }
        return Result.success(resp);
    }

    @PostMapping("/update")
    public Result<UserUpdateResp> updateUserInfo(@RequestBody UserUpdateReq req) {
        UserUpdateCmd cmd = new UserUpdateCmd(req);
        UserUpdateResp resp = userService.updateUserInfo(cmd);
        if (resp == null) {
            return Result.fail();
        }
        return Result.success();
    }

    @PostMapping("/update/password")
    public Result<UserUpdateResp> updatePassword(@RequestBody UserUpdatePasswordReq req) throws Exception {
        UserUpdatePasswordCmd cmd = new UserUpdatePasswordCmd(req);
        UserUpdatePasswordResp resp = userService.updateUserPassword(cmd);
        if (resp == null) {
            return Result.fail();
        }
        return Result.success();
    }

    @GetMapping("/getByIds")
    Result<Map<String, UserVO>> getByIds(@RequestParam("userIds") List<String> userIds) {
        Map<String, UserVO> userMap = userService.getUserByIds(userIds);
        return Result.success(userMap);
    }

    @PostMapping("/update_avatar")
    Result<FileUploadResp> upload(ImageUploadReq req) {
        FileUploadResp fileUploadResp = userService.uploadAvatar(req);
        return Result.success(fileUploadResp);
    }
}
