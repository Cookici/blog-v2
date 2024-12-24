package com.lrh.common.spring;

import org.springframework.beans.factory.InitializingBean;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.config.spring
 * @ClassName: FastJsonSafeMode
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 00:10
 */

public class FastJsonSafeMode implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty("fastjson2.parser.safeMode", "true");
    }
}

