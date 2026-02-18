package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Article;
import com.tmyx.backend.mapper.ArticleMapper;
import com.tmyx.backend.util.FileUtil;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {
    @Autowired
    private ArticleMapper articleMapper;
    // 未来需要更改为相对路径
    @Value("${file.upload-path}")
    private String baseUploadPath;

    // 获取所有已发布文章
    @GetMapping("/published")
    public Result getPublished() {
        return Result.success(articleMapper.findPublished());
    }

    // 获取所有草稿
    @GetMapping("/drafts")
    public Result getDrafts() {
        return Result.success(articleMapper.findDrafts());
    }

    // 创建新文章预处理
    @GetMapping("/preGenerateId")
    public Result preGenerateId() {
        // 生成文章id
        String articleId = UUID.randomUUID().toString().replace("-", "");

        String subPath = "/article/" + articleId + "/images/";
        File folder = new File(baseUploadPath + subPath);

        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (!success) {
                return Result.error("文件夹初始化失败");
            }
        }
        return Result.success(articleId);
    }

    // 上传图片
    @PostMapping("/uploadImage")
    public Result uploadImage(@RequestParam("file") MultipartFile file,
                              @RequestParam("articleId") String articleId) {
        if (file.isEmpty()) return Result.error("文件不能为空");

        try {
            // 确定文章的存储路径
            String subPath = "/article/" + articleId + "/images/";
            File folder = new File(baseUploadPath + subPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(folder + fileName);
            file.transferTo(dest);

            // 拼接路径
            String fileUrl = "/files" + subPath + fileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败");
        }
    }

    // 保存文章
    @PostMapping("/save")
    public Result saveArticle(@RequestBody Article article, @RequestAttribute Integer userId) {
        // 确定文件存储相对路径以及文件名
        // article/{articleId}/index.md
        String relativeFolder = "/article/" + article.getId() + "/";
        String fileName = "index.md";
        String relativeFilePath = relativeFolder + fileName;
        // 获取绝对路径
        File folder = new File(baseUploadPath + relativeFolder);
        if (!folder.exists()) folder.mkdirs();

        // 将前端传来的content写入markdown文件中
        if (article.getContent() != null) {
            try (FileWriter writer = new FileWriter(new File(folder, fileName))) {
                writer.write(article.getContent());
            } catch (IOException e) {
                e.printStackTrace();
                return Result.error("内容文件保存失败");
            }
        } else {
            System.out.println("跳过文件写入");
        }

        // 组装文章信息
        article.setId(article.getId());
        article.setUpId(userId);
        article.setContentUrl(relativeFilePath);
        article.setUploadTime(LocalDateTime.now());
        // 检查文章是否存在
        Article existArticle = articleMapper.findById(article.getId());
        if (existArticle == null) {
            articleMapper.insert(article);
        } else {
            articleMapper.update(article);
        }
        return Result.success();
    }

    // 编辑草稿文章
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable String id) {
        Article article = articleMapper.findById(id);
        if (article == null) return Result.error("文章不存在");

        // 根据contentUrl读取磁盘上的 index.md 内容
        File mdFile = new File(baseUploadPath + article.getContentUrl());
        if (mdFile.exists()) {
            try {
                String content = Files.readString(mdFile.toPath());
                article.setContent(content); // 将读取到的文本塞回实体类，传给前端
            } catch (IOException e) {
                return Result.error("读取内容文件失败");
            }
        }
        return Result.success(article);
    }

    // 删除文章（删除整个目录）
    @DeleteMapping("/delete/{id}")
    public Result deleteArticle(@PathVariable String id) {
        // 根据文章id获取文章信息
        Article article = articleMapper.findById(id);
        if (article == null) return Result.error("文章不存在");
        // 删除数据库记录
        int rows = articleMapper.deleteById(id);
        // 获取文件路径
        String fileUrl = article.getContentUrl();
        // 去除映射路径的/files前缀，再去掉markdown文件的文件名，得到相对路径
        String relativeDirPath = fileUrl.replace("/files/", "").replace("index.md", "");
        // 拼接物理路径并删除
        File articleFolder = new File(baseUploadPath + relativeDirPath);
        FileUtil.deleteDirectory(articleFolder);

        return Result.success();
    }


}
