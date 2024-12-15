package com.lrh.article.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.dto
 * @ClassName: PageDTO
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:51
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
