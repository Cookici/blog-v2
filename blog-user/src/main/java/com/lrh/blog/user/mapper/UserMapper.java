package com.lrh.blog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.blog.user.dao.UserModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.mapper
 * @ClassName: UserMapper
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 下午2:03
 */
@Mapper
public interface UserMapper extends BaseMapper<UserModel> {

}
