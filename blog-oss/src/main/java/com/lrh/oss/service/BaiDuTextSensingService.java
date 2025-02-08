package com.lrh.oss.service;

import com.lrh.oss.dto.cqe.TextSensingCmd;
import com.lrh.oss.dto.resp.TextSensingResp;

import java.io.IOException;

public interface BaiDuTextSensingService {
    TextSensingResp detectSensitiveWords(TextSensingCmd cmd) throws IOException;

}
