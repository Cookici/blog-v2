package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.dto.req.UserUpdateIpReq;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.exception.ValidException;
import com.lrh.common.util.HostUtil;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.blog.user.dto.cqe
 * @ClassName: UserUpdateIpCmd
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/21 18:05
 */
@Getter
public class UserUpdateIpCmd {

    private final String userId;

    private final String userIp;

    public UserUpdateIpCmd(@NotNull UserUpdateIpReq req, HttpServletRequest request) {
        String actualIp = HostUtil.getActualIp(request);
        if(!HostUtil.judgeIp(actualIp)){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验错误"));
        }
        if(!Objects.equals(UserContext.getUserId(), req.getUserId())){
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "非法操作"));
        }
        this.userId = UserContext.getUserId();
        this.userIp = actualIp;
    }

}
