package com.lrh.identity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.identity.dao.RoleModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.identity.mapper
 * @ClassName: RoleMapper
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 20:06
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleModel> {

    List<RoleModel> selectRoleKinds();

}
