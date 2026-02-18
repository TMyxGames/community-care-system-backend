package com.tmyx.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${file.upload-path}")
    private String baseUploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有接口生效
                .allowedOriginPatterns("*") // 允许所有源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                .allowedHeaders("*") // 允许的 Header
                .allowCredentials(true) // 允许携带 Cookie
                .maxAge(3600); // 预检请求有效期
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filePath = baseUploadPath.replace("\\", "/");
        if (!filePath.endsWith("/")) filePath += "/";

//        // 走马灯
//        registry.addResourceHandler("/carousel/**")
//                .addResourceLocations("file:" + filePath + "carousel/");
//
//        // 服务
//        registry.addResourceHandler("/service/**")
//                .addResourceLocations("file:" + filePath + "service/");
//
//        // 用户头像
//        registry.addResourceHandler("/user/**")
//                .addResourceLocations("file:" + filePath + "user/");
//
//        // 文章
//        registry.addResourceHandler("/article/**")
//                .addResourceLocations("file:" + filePath + "article/");

        // 通用映射
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + filePath)
                .setCachePeriod(3600);
    }
}
