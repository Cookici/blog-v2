package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.constant.BusinessConstant;
import com.lrh.blog.user.dto.UserValid;
import com.lrh.blog.user.dto.req.UserLoginReq;
import com.lrh.blog.user.exception.ValidException;
import lombok.Getter;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.dto.cqe
 * @ClassName: UserLoginQuery
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午3:55
 */
@Getter
public class UserLoginQuery {

    private final String userName;

    private final String userPassword;

    public UserLoginQuery(UserLoginReq req) throws ValidException {
        if (UserValid.validUserName(req.getUserName())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户名"));
        }
        if (UserValid.validUserPassword(req.getUserPassword())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "密码"));
        }
        this.userName = req.getUserName();
        this.userPassword = req.getUserPassword();
    }
}
