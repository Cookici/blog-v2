package com.lrh.message.controller;

import com.lrh.common.result.Result;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.dto.req.MessageChangeStatusReq;
import com.lrh.message.dto.req.MessagePageReq;
import com.lrh.message.netty.message.MessageVO;
import com.lrh.message.service.MessageService;
import org.springframework.web.bind.annotation.*;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.controller
 * @ClassName: MessageController
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 16:28
 */
@RestController
@RequestMapping("/api/message-netty")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/page")
    public Result<PageDTO<MessageVO>> getMessagePage(MessagePageReq req) {
        PageDTO<MessageVO> result = messageService.getMessagePage(req);
        return Result.success(result);
    }


    @GetMapping("/last-message")
    public Result<MessageVO> getLastMessage(String userId, String toUserId) {
        MessageVO result = messageService.getLastMessage(userId, toUserId);
        return Result.success(result);
    }

    @PostMapping("/change-status")
    public Result<Void> changeStatus(@RequestBody MessageChangeStatusReq req) {
        messageService.changeStatus(req);
        return Result.success();
    }

}
