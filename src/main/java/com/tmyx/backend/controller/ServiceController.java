package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Service;
import com.tmyx.backend.mapper.ServiceMapper;
import com.tmyx.backend.util.FileUtil;
import com.tmyx.backend.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/service")
@CrossOrigin
public class ServiceController {

    @Autowired
    private ServiceMapper serviceMapper;

    // 未来需要更改为相对路径
    @Value("${file.upload-path}")
    private String baseUploadPath;

    // 获取全部服务数据
    @GetMapping("/all")
    public Result getAllService() {
        List<Service> services = serviceMapper.findAll();
        return Result.success(services);
    }

    // 获取当前的服务数据
    @GetMapping("/{id}")
    public Result getServiceById(@PathVariable int id) {
        Service service = serviceMapper.findById(id);
        return Result.success(service);
    }

    // 上传Markdown文件
    @PostMapping("/upload/markdown")
    public Result uploadMarkdown(@RequestParam("id") Integer id,
                                 @RequestParam("file") MultipartFile file) throws IOException {
        // 获取绝对路径
        File baseDir = new File(baseUploadPath).getAbsoluteFile();
        String absolutePath = baseDir.getAbsolutePath();
        // 获取旧文件url
        String oldUrl = serviceMapper.findContentUrlById(id);
        // 保存新文件
        String subPath = "service/contents/";
        File uploadDir = new File(absolutePath, subPath);
        // 如果目录不存在则创建
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, fileName);
        file.transferTo(dest.getAbsoluteFile());

        // 删除旧的文件
        if (oldUrl != null && !oldUrl.isEmpty()) {
            FileUtil.checkAndDeleteFile(absolutePath, oldUrl);
        }

        return Result.success("/files/" + subPath + fileName);
    }

    // 上传图片文件1
    @PostMapping("/upload/img")
    public Result uploadImg(@RequestParam("id") Integer id,
                            @RequestParam("file") MultipartFile file) throws IOException {
        // 获取绝对路径
        File baseDir = new File(baseUploadPath).getAbsoluteFile();
        String absolutePath = baseDir.getAbsolutePath();
        // 获取旧文件url
        String oldUrl = serviceMapper.findImgUrlById(id);
        // 保存新文件
        String subPath = "service/images/";
        File uploadDir = new File(absolutePath, subPath);
        // 如果目录不存在则创建
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, fileName);
        file.transferTo(dest.getAbsoluteFile());

        // 删除旧的文件
        if (oldUrl != null && !oldUrl.isEmpty()) {
            FileUtil.checkAndDeleteFile(absolutePath, oldUrl);
        }

        return Result.success("/files/" + subPath + fileName);
    }

    // 保存服务数据
    @PostMapping("/save")
    @Transactional
    public Result save(@RequestBody List<Service> services) {
        // 获取绝对路径
        File baseDir = new File(baseUploadPath).getAbsoluteFile();
        String absolutePath = baseDir.getAbsolutePath();
        // 获取数据库中现有的服务数据
        List<Service> oldList = serviceMapper.findAll();

        List<Integer> newIds = services.stream()
                .map(Service::getId)
                .filter(id -> id != 0)
                .collect(Collectors.toList());

        for(Service oldItem : oldList) {
            if (!newIds.contains(oldItem.getId())) {
                // 从数据库中提取相应文件
                String imgUrl = oldItem.getImgUrl();
                String contentUrl = oldItem.getContentUrl();
                // 执行数据库删除
                serviceMapper.delete(oldItem.getId());
                // 清理图片
                long imgRefCount = services.stream().filter(s -> imgUrl != null && imgUrl.equals(s.getImgUrl())).count();
                if (imgRefCount == 0) {
                    FileUtil.checkAndDeleteFile(absolutePath, imgUrl);
                }
                // 清理Markdown文件
                long contentRefCount = services.stream().filter(s -> contentUrl != null && contentUrl.equals(s.getContentUrl())).count();
                if (contentRefCount == 0) {
                    FileUtil.checkAndDeleteFile(absolutePath, contentUrl);
                }
            }
        }

        for(int i = 0; i < services.size(); i++) {
            Service s = services.get(i);
            s.setSortOrder(i);

            if (s.getId() > 0) {
                serviceMapper.update(s); // 根据ID更新
            } else {
                serviceMapper.insert(s); // 没有ID时插入
            }
        }
        return Result.success();
    }

}
