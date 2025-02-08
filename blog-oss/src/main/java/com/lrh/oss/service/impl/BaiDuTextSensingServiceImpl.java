package com.lrh.oss.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrh.oss.config.BaiDuTextSensingConfig;
import com.lrh.oss.constant.TextSensingConstant;
import com.lrh.oss.dto.cqe.TextSensingCmd;
import com.lrh.oss.dto.resp.TextSensingResp;
import com.lrh.oss.service.BaiDuTextSensingService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import okhttp3.*;
import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.lrh.oss.constant.TextSensingConstant.TEXT_SENSING_LOCK;

@Slf4j
@Service
public class BaiDuTextSensingServiceImpl implements BaiDuTextSensingService {

    private final BaiDuTextSensingConfig baiDuTextSensingConfig;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedissonClient redissonClient;

    private final OkHttpClient okHttpClient;

    public BaiDuTextSensingServiceImpl(RedisTemplate<String, Object> redisTemplate, BaiDuTextSensingConfig baiDuTextSensingConfig, RedissonClient redissonClient, OkHttpClient okHttpClient) {
        this.redisTemplate = redisTemplate;
        this.baiDuTextSensingConfig = baiDuTextSensingConfig;
        this.redissonClient = redissonClient;
        this.okHttpClient = okHttpClient;
    }

    // 生成Token的URL
    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";

    // 文本检测
    private static final String SENSITIVE_WORD_API = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined?access_token=";


    private String getAccessToken() throws IOException {
        String cachedToken = (String) redisTemplate.opsForValue().get(TextSensingConstant.TEXT_SENSING_TOKEN);
        if (cachedToken != null) {
            return cachedToken;
        }
        RLock lock = redissonClient.getLock(TEXT_SENSING_LOCK);
        if (lock.tryLock()) {
            try {
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + baiDuTextSensingConfig.getApiKey() + "&client_secret=" + baiDuTextSensingConfig.getSecretKey());
                Request request = new Request.Builder().url(TOKEN_URL).method("POST", body).addHeader("Content-Type", "application/x-www-form-urlencoded").build();
                Response response = okHttpClient.newCall(request).execute();
                String token = new JSONObject(response.body().string()).getString("access_token");
                redisTemplate.opsForValue().set("baidu_access_token", TextSensingConstant.TEXT_SENSING_TOKEN, 20, TimeUnit.DAYS);
                return token;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return getAccessToken();
        }
    }

    @Override
    public TextSensingResp detectSensitiveWords(TextSensingCmd cmd) throws IOException {
        String accessToken = getAccessToken();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String postData = "text=" + cmd.getText();
        RequestBody body = RequestBody.create(mediaType, postData);
        Request request = new Request.Builder().url(SENSITIVE_WORD_API + accessToken).method("POST", body).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("Accept", "application/json").build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request failed with code: " + response.code());
            }
            String responseBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, TextSensingResp.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing response", e);
        }
    }

}
