package com.lrh.identity.dto.resp;


import com.lrh.identity.dto.ApiDTO;
import com.lrh.identity.dto.ModuleDTO;
import com.lrh.identity.dto.RoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionResp {
    private String userId;
    private List<RoleDTO> roles;
    private List<ModuleDTO> modules;
    private Map<String, List<ApiDTO>> moduleApis;
}