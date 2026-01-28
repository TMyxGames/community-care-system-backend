package com.tmyx.backend.util;

import java.io.File;

public class FileUtil {

    // 删除硬盘上的文件
    public static void checkAndDeleteFile(String baseUploadPath, String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            String basePath = baseUploadPath.replace("\\", "/");
            if (!basePath.endsWith("/")) {
                basePath += "/";
            }

            String pathOnly = fileUrl;
            if (fileUrl.startsWith("http")) {
                // 找到第三个斜杠的位置（即端口号后的那个斜杠）
                // 例如 http://localhost:8081/carousel/... 截取后变成 /carousel/...
                try {
                    java.net.URL url = new java.net.URL(fileUrl);
                    pathOnly = url.getPath();
                } catch (Exception e) {
                    // 如果 URL 格式有问题，尝试手动截取
                    int slashIndex = fileUrl.indexOf("/", fileUrl.indexOf("//") + 2);
                    pathOnly = fileUrl.substring(slashIndex);
                }
            }

            String relativePath = pathOnly.startsWith("/") ? pathOnly.substring(1) : fileUrl;
            File file = new File(basePath + relativePath);

            System.out.println("尝试删除文件，绝对路径为: " + file.getAbsolutePath());

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
}
