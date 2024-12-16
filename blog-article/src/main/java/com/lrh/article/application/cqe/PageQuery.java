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
    private Long page;
    private Long pageSize;

    public PageQuery(Long page, Long pageSize) {
        if (page <= 1) {
            page = 1L;
        }
        if (pageSize <= 0) {
            pageSize = 10L;
        }
        this.page = page;
        this.pageSize = pageSize;
    }

    public Long getOffset() {
        return (page - 1) * pageSize;
    }

    public Long getLimit() {
        return pageSize;
    }

}
