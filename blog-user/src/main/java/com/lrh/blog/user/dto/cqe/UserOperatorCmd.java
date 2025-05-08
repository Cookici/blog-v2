package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.dto.req.UserOperatorReq;
import com.lrh.blog.user.dto.valid.UserValid;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class UserOperatorCmd {
    private final String userId;

    private final Integer status;


    public UserOperatorCmd(@NotNull UserOperatorReq req) throws ValidException {
        if (UserValid.validUserId(req.getUserId())) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "用户"));
        }
        if (req.getStatus() == null) {
            throw new ValidException("错误操作");
        }
        if (!req.getStatus().equals(BusinessConstant.IS_DELETED) && !req.getStatus().equals(BusinessConstant.IS_NOT_DELETED)) {
            throw new ValidException("错误操作");
        }
        this.status = req.getStatus();
        this.userId = req.getUserId();
    }
}
