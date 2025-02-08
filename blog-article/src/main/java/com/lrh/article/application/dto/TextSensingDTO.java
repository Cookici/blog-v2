package com.lrh.article.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextSensingDTO {
    private String conclusion;

    // 检测类型
    // 1 合规 2 不合规和疑似
    private Integer conclusionType;
}
