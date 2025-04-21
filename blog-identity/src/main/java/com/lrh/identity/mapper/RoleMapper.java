package com.lrh.identity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.identity.model.RoleModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleModel> {
    /**
     * 获取所有角色
     * @return 角色列表
     */
    @Select("SELECT * FROM t_role WHERE is_deleted = 0")
    List<RoleModel> getAllRoles();
    
    /**
     * 获取用户的角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM t_user_role WHERE user_id = #{userId} AND is_deleted = 0")
    List<String> getUserRoleIds(@Param("userId") String userId);

    @Insert("INSERT INTO t_user_role(user_id, role_id, is_deleted) VALUES (#{userId}, #{roleId}, 0)")
    Integer insertUserRole(@Param("userId") String userId, @Param("roleId") String roleId);
}