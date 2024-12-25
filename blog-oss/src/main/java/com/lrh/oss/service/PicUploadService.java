package com.lrh.oss.service;

import com.lrh.oss.dto.req.ImageUploadReq;
import com.lrh.oss.dto.resp.FIleUploadResp;
import org.springframework.web.multipart.MultipartFile;

public interface PicUploadService {
    FIleUploadResp upload(ImageUploadReq uploadFile);
}
