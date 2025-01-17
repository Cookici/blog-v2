package com.lrh.article.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.util
 * @ClassName: CommonUtil
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/17 17:46
 */
@Slf4j
public class CommonUtil {

    public static Boolean judgeIp(String ip) {
        return Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$"
                , ip);
    }


}
