package com.lrh.identity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.identity.model.ModuleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ModuleMapper extends BaseMapper<ModuleModel> {

    /**
     * 获取所有模块列表
     * @return 模块列表
     */
    @Select("SELECT * FROM t_module WHERE is_deleted = 0")
    List<ModuleModel> getAllModules();

}