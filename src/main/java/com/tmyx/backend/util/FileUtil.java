package com.tmyx.backend.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
}
