package com.lrh.identity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.identity.model.RoleModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<RoleModel> {
    /**
     * 获取用户的角色ID
     * @param userId 用户ID
     * @return 角色ID
     */
    @Select("SELECT role_id FROM t_user_role WHERE user_id = #{userId} AND is_deleted = 0 LIMIT 1")
    String getUserRoleId(@Param("userId") String userId);

    @Insert("INSERT INTO t_user_role(user_id, role_id, is_deleted) VALUES (#{userId}, #{roleId}, 0)")
    Integer insertUserRole(@Param("userId") String userId, @Param("roleId") String roleId);
}