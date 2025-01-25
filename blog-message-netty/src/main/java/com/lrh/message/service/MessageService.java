package com.lrh.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.dto.req.MessageChangeStatusReq;
import com.lrh.message.dto.req.MessageGetOfflineReq;
import com.lrh.message.dto.req.MessagePageReq;
import com.lrh.message.model.MessageModel;
import com.lrh.message.netty.message.MessageVO;

import java.util.Map;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.service
 * @ClassName: MessageService
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 16:28
 */

public interface MessageService extends IService<MessageModel> {

    PageDTO<MessageVO> getMessagePage(MessagePageReq req);

    void setCache(MessageVO messageVO);

    MessageVO getLastMessage(String userId, String toUserId);

    void changeStatus(MessageChangeStatusReq req);

    Map<String,Long> getOfflineMessageCount(MessageGetOfflineReq req);
}
