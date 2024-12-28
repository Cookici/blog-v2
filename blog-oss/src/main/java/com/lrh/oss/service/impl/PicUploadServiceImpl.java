package com.lrh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.lrh.common.exception.ValidException;
import com.lrh.oss.config.AliyunConfig;
import com.lrh.oss.dto.req.ImageUploadReq;
import com.lrh.oss.dto.resp.FIleUploadResp;
import com.lrh.oss.service.PicUploadService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.io.ByteArrayInputStream;

@Service
public class PicUploadServiceImpl implements PicUploadService {


    private final OSS ossClient;  // 注入 OSS 类型的 Bean

    private final AliyunConfig aliyunConfig;

    public PicUploadServiceImpl(OSS ossClient, AliyunConfig aliyunConfig) {
        this.ossClient = ossClient;
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    public FIleUploadResp upload(ImageUploadReq cmd) {
        cmd.valid();
        String fileName = cmd.getImageFile().getOriginalFilename();
        String filePath = getFilePath(fileName);
        try {
            // 使用 OSS 客户端上传文件
            ossClient.putObject(aliyunConfig.getBucketName(), filePath, new ByteArrayInputStream(cmd.getImageFile().getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            // 上传失败
            throw new ValidException(e.getMessage());
        }
        return new FIleUploadResp(this.aliyunConfig.getUrlPrefix() + getFilePath(filePath));
    }

    // 创建文件路径
    private String getFilePath(String sourceFileName) {
        LocalDate currentDate = LocalDate.now();
        return "blog-v2-photos/" + currentDate + "/" + sourceFileName;
    }
}
