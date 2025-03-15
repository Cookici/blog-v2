package com.lrh.common.context.filter;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lrh.common.constant.PasswordKeyConstant;
import com.lrh.common.context.UserContext;
import com.lrh.common.context.UserInfoDTO;
import com.lrh.common.util.JwtUtil;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

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
            String token = httpServletRequest.getHeader(PasswordKeyConstant.AUTHORIZATION);
            if (StringUtils.hasText(token)) {
                DecodedJWT verify = JwtUtil.verify(token);
                Map<String, Claim> claims = verify.getClaims();
                String userName = String.valueOf(claims.get("userName").asString());
                String userId = String.valueOf(claims.get("userId").asString());
                UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                        .userId(userId)
                        .userName(userName)
                        .token(token)
                        .build();
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
