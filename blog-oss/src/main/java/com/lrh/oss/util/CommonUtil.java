package com.lrh.oss.util;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public class CommonUtil {
    public static String getFileSuffix(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            // 如果没有代理头，使用直接连接的地址
            ip = request.getRemoteAddr();
        } else {
            // 获取第一个IP地址（客户端的真实IP）
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
