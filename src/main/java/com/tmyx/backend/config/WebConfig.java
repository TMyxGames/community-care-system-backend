package com.tmyx.backend.config;

import com.tmyx.backend.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry  registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",              // 登录
                        "/auth/register",           // 注册
                        "/auth/reset",              // 重置密码
                        "/auth/sendRegiCaptcha",    // 发送注册验证码
                        "/auth/sendResetCaptcha",   // 发送重置密码验证码
                        "/files/**",                // 文件路径映射
                        "/carousel/all",            // 获取轮播图
                        "/service/all",             // 获取服务
                        "/service/**",              // 获取服务详情
                        "/article/published",       // 获取已发布文章
                        "/article/get/**",          // 获取文章内容
                        "/evaluation",              // 获取服务评论
                        "/user/avatars/**"          // 获取用户头像

                );
    }
}
