package com.lrh.oss.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextSensingReq {
    // 检测的文本
    private String text;
}
