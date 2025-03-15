package com.lrh.blog.user.romote.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleBindReq{
    private String userId;
    private String roleId;
}
