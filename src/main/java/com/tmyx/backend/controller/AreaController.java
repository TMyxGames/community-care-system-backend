package com.tmyx.backend.controller;

import com.tmyx.backend.entity.SafeArea;
import com.tmyx.backend.entity.ServiceArea;
import com.tmyx.backend.mapper.SafeAreaMapper;
import com.tmyx.backend.mapper.ServiceAreaMapper;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/area")
@CrossOrigin
public class AreaController {
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private SafeAreaMapper safeAreaMapper;

    // 获取所有服务区域
    @GetMapping("/service/all")
    public Result getAllServiceArea() {

        List<ServiceArea> areas = serviceAreaMapper.findAll();
        return Result.success(areas);
    }

    // 添加服务区域
    @PostMapping("/service/add")
    public String addServiceArea(@RequestBody ServiceArea area) {
        int result = serviceAreaMapper.insert(area);
        if (result > 0) {
            return "添加成功";
        } else {
            return "添加失败";
        }
    }

    // 删除服务区域
    @DeleteMapping("/service/delete/{id}")
    public String removeServiceArea(@PathVariable Integer id) {
        int result = serviceAreaMapper.delete(id);
        if (result > 0) {
            return "删除成功";
        } else {
            return "删除失败";
        }
    }



    // 获取所有安全区域
    @GetMapping("/safe/all")
    public Result getAllSafeArea() {
        List<SafeArea> areas = safeAreaMapper.findAll();
        return Result.success(areas);
    }

    // 添加服务区域
    @PostMapping("/safe/add")
    public String addSafeArea(@RequestBody SafeArea area) {
        int result = safeAreaMapper.insert(area);
        if (result > 0) {
            return "添加成功";
        } else {
            return "添加失败";
        }
    }

    // 删除安全区域
    @DeleteMapping("/safe/delete/{id}")
    public String removeSafeArea(@PathVariable Integer id) {
        int result = safeAreaMapper.delete(id);
        if (result > 0) {
            return "删除成功";
        } else {
            return "删除失败";
        }
    }

}
