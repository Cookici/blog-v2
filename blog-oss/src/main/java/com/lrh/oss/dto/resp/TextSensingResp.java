package com.lrh.oss.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextSensingResp {
    private String conclusion;

    // 检测类型
    // 1 合规 2 不合规和疑似
    private Integer conclusionType;
}
