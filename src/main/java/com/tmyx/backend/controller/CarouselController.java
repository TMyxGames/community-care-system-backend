package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Carousel;
import com.tmyx.backend.mapper.CarouselMapper;
import com.tmyx.backend.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carousel")
@CrossOrigin
public class CarouselController {

    @Autowired
    private CarouselMapper carouselMapper;

    // 未来需要更改为相对路径
    @Value("${file.upload-path}")
    private String baseUploadPath;

    // 获取轮播数据
    @GetMapping("/all")
    public List<Carousel> getAllCarousels() {
        return carouselMapper.findAll();
    }

    // 上传图片文件
    @PostMapping("/upload/img")
    public String uploadImg(@RequestParam("file") MultipartFile file,
                            @RequestParam(value = "oldUrl", required = false) String oldUrl) throws IOException {

        // 保存新文件
        File uploadDir = new File(baseUploadPath, "carousel/images/");
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

        return "/carousel/images/" + fileName;
    }

    // 保存轮播数据
    @PostMapping("/save")
    @Transactional
    public String save(@RequestBody List<Carousel> carousels) {
        // 获取数据库中现有的轮播数据
        List<Carousel> oldList = carouselMapper.findAll();

        List<Integer> newIds = carousels.stream()
                .map(Carousel::getId)
                .filter(id -> id != 0)
                .collect(Collectors.toList());

        for(Carousel oldItem : oldList) {
            if (!newIds.contains(oldItem.getId())) {
                // 从数据库中提取相应文件
                String imgUrl = oldItem.getImgUrl();
                // 执行数据库删除
                carouselMapper.delete(oldItem.getId());
                // 清理图片
                long imgRefCount = carousels.stream().filter(c -> imgUrl != null && imgUrl.equals(c.getImgUrl())).count();
                if (imgRefCount == 0) {
                    FileUtil.checkAndDeleteFile(baseUploadPath, imgUrl);
                }
            }
        }

        for(int i = 0; i < carousels.size(); i++) {
            Carousel c = carousels.get(i);
            c.setSortOrder(i);

            if (c.getId() > 0) {
                carouselMapper.update(c); // 根据ID更新
            } else {
                carouselMapper.insert(c); // 没有ID时插入
            }
        }
        return "轮播数据保存成功";
    }

    // 删除硬盘上的文件（已弃用)
//    private void checkAndDeleteFile(String imgUrl) {
//        if (imgUrl == null || imgUrl.isEmpty()) return;
//        try {
//            // 从 URL 中提取文件名 (例如从 /images/carousel/xxx.jpg 提取 xxx.jpg)
//            String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
//            String relativePath = baseUploadPath + "carousel/images/";
//            File file = new File(relativePath, fileName);
//            if (file.exists()) {
//                file.delete();
//                System.out.println("文件删除成功: " + fileName);
//            }
//        } catch (Exception e) {
//            System.err.println("文件删除失败: " + e.getMessage());
//        }
//    }
}
