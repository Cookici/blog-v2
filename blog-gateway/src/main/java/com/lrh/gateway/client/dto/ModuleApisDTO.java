package com.lrh.gateway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleApisDTO {
    private List<ModuleDTO> modules;
    private Map<String, ApiDTO> moduleApis;
}
