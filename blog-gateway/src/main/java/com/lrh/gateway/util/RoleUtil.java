package com.lrh.gateway.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class RoleUtil {

    public static String buildRoleString(List<String> roleIdList) {
        return String.join(",", roleIdList);
    }

    public static List<String> getRoleIdList(String roleIds) {
        return Arrays.asList(roleIds.split(","));
    }

}
