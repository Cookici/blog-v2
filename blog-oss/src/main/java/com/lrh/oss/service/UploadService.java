package com.lrh.oss.service;

import com.lrh.oss.dto.cqe.ImageUploadCmd;
import com.lrh.oss.dto.resp.FIleUploadResp;

public interface UploadService {
    FIleUploadResp upload(ImageUploadCmd uploadFile);
}
