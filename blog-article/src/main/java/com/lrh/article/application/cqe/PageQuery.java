package com.lrh.article.application.cqe;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.cqe
 * @ClassName: PageQuery
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:31
 */

@Data
@NoArgsConstructor
@Builder
public class PageQuery {
    private Integer page;
    private Integer pageSize;

    public PageQuery(Integer page, Integer pageSize) {
        if (page <= 1) {
            page = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        this.page = page;
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }

    public Integer getLimit() {
        return pageSize;
    }

}
