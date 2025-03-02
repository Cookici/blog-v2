package com.lrh.article.infrastructure.client;

import com.lrh.article.application.dto.TextSensingDTO;
import com.lrh.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "blog-oss")
public interface OssClient {
    @GetMapping("/api/oss/file/text/sensing")
    Result<TextSensingDTO> textSensing(@RequestParam("text") String text);
}
