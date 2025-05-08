package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.dto.req.UserAdminSearchPageReq;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class UserAdminSearchPageCmd {
    private final String keyword;
    private final Long pageNum;
    private final Long pageSize;
    private final Integer status;

    public UserAdminSearchPageCmd(@NotNull UserAdminSearchPageReq req) throws ValidException {
        if(req.getKeyword() == null){
            throw new ValidException("关键字不能为空");
        }
        if (req.getPageNum() == null || req.getPageNum() < 1) {
            throw new ValidException("页码不能为空");
        }
        if(req.getPageSize() == null || req.getPageSize() < 1){
            throw new ValidException("每页条数不能为空");
        }
        if (req.getStatus() != null && !req.getStatus().equals(BusinessConstant.IS_DELETED) && !req.getStatus().equals(BusinessConstant.IS_NOT_DELETED)) {
            throw new ValidException("错误操作");
        }
        this.keyword = req.getKeyword();
        this.pageNum = req.getPageNum();
        this.pageSize = req.getPageSize();
        this.status = req.getStatus();
    }
}
