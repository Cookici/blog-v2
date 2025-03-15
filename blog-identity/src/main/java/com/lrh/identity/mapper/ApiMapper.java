package com.lrh.identity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.identity.model.ApiModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiMapper extends BaseMapper<ApiModel> {
    @Select("SELECT DISTINCT a.* FROM t_api_resource a " +
            "LEFT JOIN t_role_api ra ON a.api_id = ra.api_id " +
            "LEFT JOIN t_user_role ur ON ra.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND a.module_id = #{moduleId} AND a.is_deleted = 0")
    List<ApiModel> getModuleApis(@Param("userId") String userId, @Param("moduleId") String moduleId);
}