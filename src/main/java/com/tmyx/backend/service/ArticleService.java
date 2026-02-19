package com.tmyx.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArticleService {
    // 未来需要更改为相对路径
    @Value("${file.upload-path}")
    private String baseUploadPath;

    // 清理文章中未被引用的图片
    public void cleanOrphanImages(String content, String articleId) {
        // 1. 正则匹配出内容中所有的图片文件名
        Set<String> activeImages = new HashSet<>();
        Pattern pattern = Pattern.compile("/article/" + articleId + "/images/([^ )\"'\\n]+)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            activeImages.add(matcher.group(1)); // 拿到文件名
        }

        // 2. 扫描磁盘上的 images 文件夹
        File folder = new File(baseUploadPath, "article/" + articleId + "/images/");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 3. 如果磁盘文件不在内容列表里，删除之
                    if (!activeImages.contains(file.getName())) {
                        file.delete();
                        System.out.println("清理冗余图片: " + file.getName());
                    }
                }
            }
        }
    }
}
