package com.lrh.blog.user.romote;

import com.lrh.blog.user.config.FeignMultipartConfig;
import com.lrh.blog.user.dto.req.ImageUploadReq;
import com.lrh.blog.user.dto.resp.FileUploadResp;
import com.lrh.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "blog-oss", configuration = FeignMultipartConfig.class)
public interface OssClient {
    @PostMapping(value = "/api/oss/file/upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileUploadResp> upload(ImageUploadReq req);
}
