package com.tmyx.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SIGN_KEY = "tmyx_secret_key"; // 签名密钥
    private static final Long EXPIRE = 43200000L; // 12小时过期

    // 生成 Token
    public static String createToken(Integer userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SIGN_KEY)
                .compact();
    }

    // 解析 Token
    public static Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(SIGN_KEY).parseClaimsJws(token).getBody();
    }
}
