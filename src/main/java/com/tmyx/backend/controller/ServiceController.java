package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Carousel;
import com.tmyx.backend.entity.Service;
import com.tmyx.backend.mapper.ServiceMapper;
import com.tmyx.backend.util.FileUtil;
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
    public List<Service> getAllService() { return serviceMapper.findAll(); }

    // 获取当前的服务数据
    @GetMapping("/{id}")
    public Service getServiceById(@PathVariable int id) { return serviceMapper.findById(id); }

    // 上传Markdown文件
    @PostMapping("/upload/markdown")
    public String uploadMarkdown(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "oldUrl", required = false) String oldUrl) throws IOException {
        // 保存新文件
        File uploadDir = new File(baseUploadPath, "service/contents/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, fileName);
        file.transferTo(dest);

        // 删除旧的文件
        if (oldUrl != null && !oldUrl.isEmpty()) {
            FileUtil.checkAndDeleteFile(baseUploadPath, oldUrl);
        }

        return "/service/contents/" + fileName;
    }

    // 上传图片文件
    @PostMapping("/upload/img")
    public String uploadImg(@RequestParam("file") MultipartFile file,
                            @RequestParam(value = "oldUrl", required = false) String oldUrl) throws IOException {

        // 保存新文件
        File uploadDir = new File(baseUploadPath, "service/images/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, fileName);
        file.transferTo(dest);

        // 删除旧的文件
        if (oldUrl != null && !oldUrl.isEmpty()) {
            FileUtil.checkAndDeleteFile(baseUploadPath, oldUrl);
        }

        return "/service/images/" + fileName;
    }

    // 保存服务数据
    @PostMapping("/save")
    @Transactional
    public String save(@RequestBody List<Service> services) {
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
                    FileUtil.checkAndDeleteFile(baseUploadPath, imgUrl);
                }
                // 清理Markdown文件
                long contentRefCount = services.stream().filter(s -> contentUrl != null && contentUrl.equals(s.getContentUrl())).count();
                if (contentRefCount == 0) {
                    FileUtil.checkAndDeleteFile(baseUploadPath, contentUrl);
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
        return "服务数据保存成功";
    }

    // 删除硬盘上的文件（已弃用）
//    private void checkAndDeleteFile(String fileUrl) {
//        if (fileUrl == null || fileUrl.isEmpty()) return;
//        try {
//            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//            String subDir = "";
//
//            if (fileUrl.contains("/images/")) {
//                subDir = "service/images/";
//            } else if (fileUrl.contains("/contents/")){
//                subDir = "service/contents/";
//            } else {
//                return;
//            }
//
//            File file = new File(baseUploadPath + subDir, fileName);
//            if (file.exists()) {
//                if(file.delete()) {
//                    System.out.println("文件删除成功: " + file.getAbsolutePath());
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("文件删除异常: " + e.getMessage());
//        }
//    }
}
