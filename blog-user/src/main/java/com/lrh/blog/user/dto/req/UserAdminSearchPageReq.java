package com.lrh.blog.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminSearchPageReq extends PageReq{

    private String keyword;

    private Integer status;

}
