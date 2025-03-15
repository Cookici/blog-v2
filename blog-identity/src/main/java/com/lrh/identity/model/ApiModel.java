package com.lrh.identity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_api_resource")
public class ApiModel {
    private Long id;
    private String apiId;
    private String moduleId;
    private String apiName;
    private String apiPath;
    private String apiMethod;
    private String apiDesc;
    private Integer isDeleted;
}