package com.lrh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.lrh.common.annotations.SubmitOnceRecords;
import com.lrh.common.exception.ValidException;
import com.lrh.oss.config.AliyunConfig;
import com.lrh.oss.dto.cqe.ImageUploadCmd;
import com.lrh.oss.dto.resp.FileUploadResp;
import com.lrh.oss.service.UploadService;
import com.lrh.oss.util.CommonUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {


    private final OSS ossClient;  // 注入 OSS 类型的 Bean

    private final AliyunConfig aliyunConfig;

    public UploadServiceImpl(OSS ossClient, AliyunConfig aliyunConfig) {
        this.ossClient = ossClient;
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    @SubmitOnceRecords(key = "oss-upload", userLabel = "#cmd.ip", expireTime = 30)
    public FileUploadResp upload(ImageUploadCmd cmd) {
        String fileName = UUID.randomUUID() + CommonUtil.getFileSuffix(cmd.getImageFile());
        String filePath = getFilePath(fileName);
        try {
            // 使用 OSS 客户端上传文件
            ossClient.putObject(aliyunConfig.getBucketName(), filePath, new ByteArrayInputStream(cmd.getImageFile().getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            // 上传失败
            throw new ValidException(e.getMessage());
        }
        return new FileUploadResp(this.aliyunConfig.getUrlPrefix() + getFilePath(filePath));
    }

    // 创建文件路径
    private String getFilePath(String sourceFileName) {
        LocalDate currentDate = LocalDate.now();
        return "blog-v2-photos/" + currentDate + "/" + sourceFileName;
    }
}
