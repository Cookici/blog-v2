package com.lrh.identity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.identity.model.RoleModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleModel> {
    @Select("SELECT r.* FROM t_role r " +
            "LEFT JOIN t_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = 0 AND ur.is_deleted = 0")
    List<RoleModel> getUserRoles(@Param("userId") String userId);

    @Insert("INSERT INTO t_user_role(user_id, role_id, is_deleted) values (#{userId}, #{roleId}, 0)")
    Integer insertUserRole(@Param("userId") String userId,@Param("roleId") String roleId);
}