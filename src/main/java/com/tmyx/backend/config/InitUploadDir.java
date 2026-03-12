package com.tmyx.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class InitUploadDir implements CommandLineRunner {
    @Value("../uploads")
    private String uploadPath;

    @Override
    public void run(String... args) throws Exception {
        try {
            Path path = Paths.get(uploadPath);
            if (Files.notExists(path)) {
                Files.createDirectories(path);
                log.info("🚀 上传目录不存在，已自动创建: {}", path.toAbsolutePath());
            } else {
                log.info("✅ 上传目录已存在: {}", path.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("❌ 创建上传目录失败: {}", e.getMessage());
        }
    }
}
