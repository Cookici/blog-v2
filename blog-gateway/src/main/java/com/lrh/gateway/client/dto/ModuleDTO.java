package com.lrh.gateway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO {
    private String moduleId;
    private String moduleName;
    private String modulePrefix;
    private String moduleDesc;
    private String parentId;
}