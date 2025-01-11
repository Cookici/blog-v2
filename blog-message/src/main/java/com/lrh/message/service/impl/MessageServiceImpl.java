package com.lrh.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrh.message.mapper.MessageMapper;
import com.lrh.message.model.MessageModel;
import com.lrh.message.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.service.impl
 * @ClassName: MessageServiceImpl
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 16:29
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageModel> implements MessageService {

}
