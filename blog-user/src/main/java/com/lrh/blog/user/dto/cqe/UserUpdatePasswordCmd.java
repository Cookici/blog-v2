package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.constant.DESConstant;
import com.lrh.blog.user.dto.req.UserUpdatePasswordReq;
import com.lrh.blog.user.dto.valid.UserValid;
import com.lrh.blog.user.util.DESUtil;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.exception.ValidException;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.blog.user.dto.cqe
 * @ClassName: UserUpdatePasswordCmd
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/17 23:59
 */
@Getter
public class UserUpdatePasswordCmd {

    private final String userId;

    private final String userPassword;

    private final String newUserPassword;

    public UserUpdatePasswordCmd(@NotNull UserUpdatePasswordReq req) throws Exception {
        if(!Objects.equals(UserContext.getUserId(), req.getUserId())){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "非法操作"));
        }
        if (UserValid.validUserId(req.getUserId())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验错误"));
        }
        if (UserValid.validUserPassword(req.getUserPassword())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "密码"));
        }
        if (UserValid.validUserPassword(req.getNewUserPassword())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "密码"));
        }
        if (UserValid.validUserPassword(req.getConfirmUserPassword())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "密码"));
        }
        if (!req.getConfirmUserPassword().equals(req.getNewUserPassword())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "两次密码不一致"));
        }
        this.userId = req.getUserId();
        this.userPassword = DESUtil.encrypt(req.getUserPassword(), DESConstant.PASSWORD_KEY);
        this.newUserPassword = DESUtil.encrypt(req.getNewUserPassword(), DESConstant.PASSWORD_KEY);
    }

}
