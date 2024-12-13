package com.lrh.identity.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrh.identity.dao.RoleModel;
import com.lrh.identity.mapper.RoleMapper;
import com.lrh.identity.service.RoleService;
import com.lrh.identity.vo.RoleVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.identity.service.impl
 * @ClassName: RoleServiceImpl
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 20:10
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleModel> implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleVO> getRoleKindsList() {
        List<RoleModel> roleModels = roleMapper.selectRoleKinds();
        if (roleModels == null) {
            return new ArrayList<>();
        }

        List<RoleVO> roleVOList = new ArrayList<>();
        for (RoleModel roleModel : roleModels) {
            RoleVO roleVO = new RoleVO().convertedRoleModelToRoleVO(roleModel);
            roleVOList.add(roleVO);
        }
        return roleVOList;
    }
}
