package com.lrh.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto
 * @ClassName: PageDto
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/22 19:53
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO<T> {
    private List<T> data;
    private Long total;
    private Long page;
    private Long pageSize;
}
