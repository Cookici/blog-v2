package com.lrh.identity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.identity.model.ModuleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ModuleMapper extends BaseMapper<ModuleModel> {
    @Select("SELECT DISTINCT m.* FROM t_module m " +
            "LEFT JOIN t_api_resource a ON m.module_id = a.module_id " +
            "LEFT JOIN t_role_api ra ON a.api_id = ra.api_id " +
            "LEFT JOIN t_user_role ur ON ra.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.is_deleted = 0")
    List<ModuleModel> getUserModules(@Param("userId") String userId);
}