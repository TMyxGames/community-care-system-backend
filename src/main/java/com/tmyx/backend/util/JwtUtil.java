package com.tmyx.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String SIGN_KEY = "tmyx_secret_key_tmyx_secret_key_tmyx"; // 签名密钥
    private static final Long EXPIRE = 43200000L; // 12小时过期
    private static final Key KEY = Keys.hmacShaKeyFor(SIGN_KEY.getBytes());

    // 生成 Token
    public static String createToken(Integer userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
