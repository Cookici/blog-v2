package com.lrh.blog.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto
 * @ClassName: PageDTO
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/22 19:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageReq {

    private Long pageNum;
    private Long pageSize;

    public Long getLimit() {
        return pageSize;
    }

    public Long getOffset() {
        return (pageNum - 1) * pageSize;
    }

}
