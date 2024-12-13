package com.lrh.common.openfeign;

import com.lrh.common.constant.PasswordKeyConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.common.context.filter
 * @ClassName: CustomFeignRequestInterceptor
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 16:52
 */
@Slf4j
public class CustomFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes reqAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) reqAttributes).getRequest();
        String userInfoJson = request.getHeader(PasswordKeyConstant.AUTHORIZATION);
        log.info("[CustomFeignRequestInterceptor] apply , Authorization is {}",userInfoJson);
        requestTemplate.header(PasswordKeyConstant.AUTHORIZATION, userInfoJson);
    }
}
