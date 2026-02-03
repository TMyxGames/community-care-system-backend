package com.tmyx.backend.interceptor;

import com.tmyx.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        // 校验token
        if (token == null || token.isEmpty()) {
            response.setStatus(401); // 401: 未授权
            return false;
        }
        try {
            // 解析token
            Claims claims = JwtUtil.parseToken(token);
            // 从token中解析用户id
            Integer userId = (Integer) claims.get("userId");
            // 将用户id保存到request中
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            response.setStatus(401); // 401: 未授权
            return false;
        }
    }

}
