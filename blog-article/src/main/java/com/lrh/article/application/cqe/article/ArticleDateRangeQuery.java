package com.lrh.article.application.cqe.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章日期范围查询
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDateRangeQuery {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endDate;
    
    public void valid() {
        if (startDate == null || endDate == null) {
            throw new ValidException("开始日期和结束日期不能为空");
        }
        
        if (endDate.isBefore(startDate)) {
            throw new ValidException("结束日期不能早于开始日期");
        }
        
        // 限制查询范围不超过31天
        if (startDate.plusDays(31).isBefore(endDate)) {
            throw new ValidException("查询日期范围不能超过31天");
        }
    }
}