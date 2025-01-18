package com.lrh.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.message.model.MessageModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.mapper
 * @ClassName: MessageMapper
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 16:30
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageModel> {

}
