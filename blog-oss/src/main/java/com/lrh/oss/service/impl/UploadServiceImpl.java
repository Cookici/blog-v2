package com.lrh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.lrh.common.annotations.SubmitOnceRecords;
import com.lrh.common.exception.ValidException;
import com.lrh.oss.config.AliyunConfig;
import com.lrh.oss.dto.cqe.ImageUploadCmd;
import com.lrh.oss.dto.resp.FIleUploadResp;
import com.lrh.oss.service.UploadService;
import com.lrh.oss.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.io.ByteArrayInputStream;
import java.util.UUID;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {


    private final OSS ossClient;

    private final AliyunConfig aliyunConfig;

    public UploadServiceImpl(OSS ossClient, AliyunConfig aliyunConfig) {
        this.ossClient = ossClient;
        this.aliyunConfig = aliyunConfig;
    }

    @Override
    @SubmitOnceRecords(key = "oss-upload", userLabel = "#cmd.ip")
    public FIleUploadResp upload(ImageUploadCmd cmd) {
        String fileName = UUID.randomUUID() + CommonUtil.getFileSuffix(cmd.getImageFile());
        String filePath = getFilePath(fileName);
        try {
            ossClient.putObject(aliyunConfig.getBucketName(), filePath, new ByteArrayInputStream(cmd.getImageFile().getBytes()));
        } catch (Exception e) {
            log.info("[UploadServiceImpl] FIleUploadResp error: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        return new FIleUploadResp(this.aliyunConfig.getUrlPrefix() + getFilePath(filePath));
    }

    /**
     * 创建文件路径
     * @param sourceFileName sourceFileName
     * @return FileName
     */
    private String getFilePath(String sourceFileName) {
        LocalDate currentDate = LocalDate.now();
        return "blog-v2-photos/" + currentDate + "/" + sourceFileName;
    }
}
