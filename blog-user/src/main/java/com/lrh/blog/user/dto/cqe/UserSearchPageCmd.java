package com.lrh.blog.user.dto.cqe;

import com.lrh.blog.user.dto.req.UserSearchPageReq;
import com.lrh.common.exception.ValidException;
import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.blog.user.dto.cqe
 * @ClassName: UserSearchPageCmd
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/23 20:09
 */
@Getter
public class UserSearchPageCmd {
    private final String keyword;
    private final Long pageNum;
    private final Long pageSize;

    public UserSearchPageCmd(@NotNull UserSearchPageReq req) throws ValidException {
        if(req.getKeyword() == null || req.getKeyword().isEmpty()){
            throw new ValidException("关键字不能为空");
        }
        if (req.getPageNum() == null || req.getPageNum() < 1) {
            throw new ValidException("页码不能为空");
        }
        if(req.getPageSize() == null || req.getPageSize() < 1){
            throw new ValidException("每页条数不能为空");
        }
        this.keyword = req.getKeyword();
        this.pageNum = req.getPageNum();
        this.pageSize = req.getPageSize();
    }

}
