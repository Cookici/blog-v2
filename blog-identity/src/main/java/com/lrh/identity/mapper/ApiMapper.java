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
     * 获取所有API
     * @return API列表
     */
    @Select("SELECT * FROM t_api_resource WHERE is_deleted = 0 ORDER BY sort_order ASC")
    List<ApiModel> getAllApis();
    
    /**
     * 获取角色的API ID列表
     * @param roleId 角色ID
     * @return API ID列表
     */
    @Select("SELECT api_id FROM t_role_api WHERE role_id = #{roleId} AND is_deleted = 0")
    List<String> getRoleApiIds(@Param("roleId") String roleId);
}