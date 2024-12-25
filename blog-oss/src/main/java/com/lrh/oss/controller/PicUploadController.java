package com.lrh.oss.controller;

import com.lrh.common.result.Result;
import com.lrh.oss.dto.req.ImageUploadReq;
import com.lrh.oss.dto.resp.FIleUploadResp;
import com.lrh.oss.service.PicUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/oss")
public class PicUploadController {
    @Autowired
    private PicUploadService picUploadService;

    @PostMapping("/upload")
    @ResponseBody
    public Result<FIleUploadResp> upload(@RequestParam("file") MultipartFile imageFile) throws Exception {
        ImageUploadReq imageUploadReq = new ImageUploadReq(imageFile);
        FIleUploadResp resp = picUploadService.upload(imageUploadReq);
        if (resp == null) {
            return Result.fail();
        }
        return Result.success(resp);
    }
}
