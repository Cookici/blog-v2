package com.lrh.gateway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiDTO {
    private String apiId;
    private String moduleId;
    private String apiName;
    private String apiPath;
    private String apiMethod;
    private String apiDesc;
}