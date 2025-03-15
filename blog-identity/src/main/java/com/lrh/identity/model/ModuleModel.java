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
@TableName("t_module")
public class ModuleModel {
    private Long id;
    private String moduleId;
    private String moduleName;
    private String modulePrefix;
    private String moduleDesc;
    private String parentId;
    private Integer isDeleted;
}