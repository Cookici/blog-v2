package com.lrh.gateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.blog.user.common.util
 * @ClassName: JwtUtil
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/12 20:47
 */

public class JwtUtil {

        /**
         * 生成token  header.payload.singature
         */
        private static final String SING = "LRH20030706";

        public static String getToken(Map<String, String> map) {

            Calendar instance = Calendar.getInstance();

            instance.add(Calendar.HOUR, 2);

            //创建jwt builder
            JWTCreator.Builder builder = JWT.create();

            // payload
            map.forEach(builder::withClaim);
            //指定令牌过期时间
            // sign
            return builder.withExpiresAt(instance.getTime())  //指定令牌过期时间
                    .sign(Algorithm.HMAC256(SING));
        }


        public static DecodedJWT verify(String token) {
            return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
        }


}
