package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Carousel;
import com.tmyx.backend.mapper.CarouselMapper;
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
    public Result getAllCarousels() {
        List<Carousel> carousels = carouselMapper.findAll();
        return Result.success(carousels);
    }

    // 上传图片文件
    @PostMapping("/upload/img")
    public Result uploadImg(@RequestParam("id") Integer id,
                            @RequestParam("file") MultipartFile file) throws IOException {
        // 获取绝对路径
        File baseDir = new File(baseUploadPath).getAbsoluteFile();
        String absolutePath = baseDir.getAbsolutePath();
        // 获取旧文件url
        String oldUrl = carouselMapper.findImgUrlById(id);
        // 保存新文件
        String subPath = "carousel/images/";
        File uploadDir = new File(baseDir, subPath);
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

    // 保存轮播数据
    @PostMapping("/save")
    @Transactional
    public Result save(@RequestBody List<Carousel> carousels) {
        // 获取绝对路径
        File baseDir = new File(baseUploadPath).getAbsoluteFile();
        String absolutePath = baseDir.getAbsolutePath();
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
                    FileUtil.checkAndDeleteFile(absolutePath, imgUrl);
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
        return Result.success();
    }

}
