package com.lrh.oss.controller;

import com.lrh.common.result.Result;
import com.lrh.oss.dto.cqe.ImageUploadCmd;
import com.lrh.oss.dto.cqe.TextSensingCmd;
import com.lrh.oss.dto.req.ImageUploadReq;
import com.lrh.oss.dto.req.TextSensingReq;
import com.lrh.oss.dto.resp.FileUploadResp;
import com.lrh.oss.dto.resp.TextSensingResp;
import com.lrh.oss.service.BaiDuTextSensingService;
import com.lrh.oss.service.UploadService;
import com.lrh.oss.util.CommonUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/oss/file")
public class UploadController {

    private final UploadService picUploadService;

    private final BaiDuTextSensingService baiDuTextSensingService;

    public UploadController(UploadService picUploadService, BaiDuTextSensingService baiDuTextSensingService) {
        this.picUploadService = picUploadService;
        this.baiDuTextSensingService = baiDuTextSensingService;
    }

    @PostMapping("/upload")
    @ResponseBody
    public Result<FileUploadResp> upload(ImageUploadReq req, HttpServletRequest request) throws Exception {
        req.setIp(CommonUtil.getClientIp(request));
        ImageUploadCmd cmd = new ImageUploadCmd(req);
        FileUploadResp resp = picUploadService.upload(cmd);
        if (resp == null) {
            return Result.fail();
        }
        return Result.success(resp);
    }

    @GetMapping("/text/sensing")
    public Result<TextSensingResp> textSensing(TextSensingReq req) throws Exception {
        TextSensingCmd cmd = new TextSensingCmd(req);
        TextSensingResp resp = baiDuTextSensingService.detectSensitiveWords(cmd);
        return Result.success(resp);
    }

}
