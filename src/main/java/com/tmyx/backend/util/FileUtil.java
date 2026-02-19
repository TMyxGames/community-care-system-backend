package com.tmyx.backend.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {

    // 删除硬盘上的文件
    public static void checkAndDeleteFile(String baseUploadPath, String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            // 提取纯净路径（处理可能存在http前缀的情况）
            String pathOnly = fileUrl;
            if (fileUrl.startsWith("http")) {
                pathOnly = new java.net.URL(fileUrl).getPath();
            }
            // 去除映射路径的/files前缀
            if (pathOnly.startsWith("/files/")) {
                pathOnly = pathOnly.substring(7); // 去掉"/files/"这7个字符
            } else if (pathOnly.startsWith("files/")) {
                pathOnly = pathOnly.substring(6); // 如果不以斜杠开头那就去6个
            }

            Path fullPath = Paths.get(baseUploadPath, pathOnly);
            File file = fullPath.toFile();

            System.out.println("尝试删除文件，物理路径为: " + file.getAbsolutePath());

            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    System.out.println("文件删除成功: " + file.getAbsolutePath());
                } else {
                    System.err.println("文件删除失败: " + file.getAbsolutePath());
                }
            } else {
                System.err.println("文件不存在或无法删除: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("文件删除异常: " + e.getMessage());
        }
    }

    // 删除硬盘上的整个目录
    public static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    // 删除文章中未使用的图片
    public static void cleanOrphanImages(String content, String articleId, String baseUploadPath) {
        if (content == null || articleId == null) return;

        // 1. 提取源码中所有引用的文件名
        // 匹配格式如：/files/article/uuid/images/filename.png
        Set<String> activeImageNames = new HashSet<>();
        String regex = "/files/article/" + articleId + "/images/([^ )\"'\\n]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            activeImageNames.add(matcher.group(1)); // 提取文件名部分
        }

        // 2. 定位物理文件夹：uploads/article/uuid/images/
        Path imagesDirPath = Paths.get(baseUploadPath, "article", articleId, "images");
        File imagesDir = imagesDirPath.toFile();

        if (imagesDir.exists() && imagesDir.isDirectory()) {
            File[] files = imagesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 3. 如果磁盘上的文件不在源码引用名单中，则删除
                    if (!activeImageNames.contains(file.getName())) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            System.out.println("成功清理冗余图片: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }
}
