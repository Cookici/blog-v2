package com.lrh.identity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrh.identity.model.RoleModel;
import com.lrh.identity.vo.RoleVO;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.identity.service
 * @ClassName: RoleServuce
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 20:09
 */

public interface RoleService extends IService<RoleModel> {

    List<RoleVO> getRoleKindsList();

}
