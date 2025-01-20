package com.lrh.blog.user.dto.cqe;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.blog.user.constant.DESConstant;
import com.lrh.blog.user.dto.valid.UserValid;
import com.lrh.blog.user.dto.req.UserRegisterReq;
import com.lrh.common.exception.ValidException;
import com.lrh.blog.user.util.DESUtil;
import com.lrh.common.util.HostUtil;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto.cqe
 * @ClassName: UserRegisterCmd
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午4:01
 */
@Getter
public class UserRegisterCmd {
    private final String userName;

    private final String userPassword;

    private final String userPhone;

    private final String userSex;

    private final LocalDateTime userBirthday;

    private final String userIp;

    private final String userEmail;

    public UserRegisterCmd(@NotNull UserRegisterReq req, HttpServletRequest httpServletRequest) throws Exception {
        if (UserValid.validUserPassword(req.getUserPassword())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "密码"));
        }
        if (UserValid.validUserPassword(req.getUserPasswordAgain())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "密码"));
        }
        if (!req.getUserPasswordAgain().equals(req.getUserPassword())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "两次密码不一致"));
        }
        if (UserValid.validUserName(req.getUserName())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户名"));
        }
        if (UserValid.validUserPhone(req.getUserPhone())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "电话"));
        }
        if (UserValid.validUserSex(req.getUserSex())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户性别"));
        }
        if (UserValid.validUserBirthday(req.getUserBirthday())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户生日"));
        }
        if (httpServletRequest == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        String remoteAddr = HostUtil.getActualIp(httpServletRequest);
        if (remoteAddr == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (!HostUtil.judgeIp(remoteAddr)) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (UserValid.validUserEmail(req.getUserEmail())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        this.userName = req.getUserName();
        this.userPassword = DESUtil.encrypt(req.getUserPassword(), DESConstant.PASSWORD_KEY);
        this.userPhone = req.getUserPhone();
        this.userSex = req.getUserSex();
        this.userBirthday = req.getUserBirthday();
        this.userEmail = req.getUserEmail();
        this.userIp = remoteAddr;
    }

}
