package com.lrh.oss.service;

import com.lrh.oss.dto.cqe.ImageUploadCmd;
import com.lrh.oss.dto.resp.FIleUploadResp;

public interface UploadService {
    /**
     * 文件上传
     * @param uploadFile 请求参数
     * @return 返回值
     */
    FIleUploadResp upload(ImageUploadCmd uploadFile);
}
