package com.lrh.oss.service;

import com.lrh.oss.dto.cqe.ImageUploadCmd;
import com.lrh.oss.dto.resp.FileUploadResp;

public interface UploadService {
    FileUploadResp upload(ImageUploadCmd uploadFile);
}
