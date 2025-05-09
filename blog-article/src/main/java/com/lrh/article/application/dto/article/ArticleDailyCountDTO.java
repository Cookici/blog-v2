package com.lrh.article.application.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 文章每日数量统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDailyCountDTO {
    
    private LocalDate date;
    private Long count;
}