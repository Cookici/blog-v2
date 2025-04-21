package com.lrh.identity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.identity.model.ApiModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApiMapper extends BaseMapper<ApiModel> {
    
    /**
     * 获取模块的所有API列表
     * @param moduleId 模块ID
     * @return API列表
     */
    @Select("SELECT * FROM t_api_resource WHERE module_id = #{moduleId} AND is_deleted = 0")
    List<ApiModel> getApisByModuleId(@Param("moduleId") String moduleId);
    
    /**
     * 获取角色拥有的API ID列表
     * @param roleId 角色ID
     * @return API ID列表
     */
    @Select("SELECT api_id FROM t_role_api WHERE role_id = #{roleId} AND is_deleted = 0")
    List<String> getRoleApiIds(@Param("roleId") String roleId);
    
    /**
     * 获取所有API列表
     * @return API列表
     */
    @Select("SELECT * FROM t_api_resource WHERE is_deleted = 0")
    List<ApiModel> getAllApis();
}