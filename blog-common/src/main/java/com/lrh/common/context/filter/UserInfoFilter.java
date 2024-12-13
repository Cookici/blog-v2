package com.lrh.common.context.filter;

import com.alibaba.fastjson2.JSON;
import com.lrh.common.constant.PasswordKeyConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.context.UserInfoDTO;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.filter
 * @ClassName: TokenFilter
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/13 13:07
 */

public class UserInfoFilter implements Filter, Ordered {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String userInfoDTOJson = httpServletRequest.getHeader(PasswordKeyConstant.Authorization);
            if (StringUtils.hasText(userInfoDTOJson)) {
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoDTOJson, UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
            chain.doFilter(request, response);
        } finally {
            UserContext.removeUser();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
