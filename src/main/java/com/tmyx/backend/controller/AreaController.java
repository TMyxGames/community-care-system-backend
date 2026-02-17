package com.tmyx.backend.controller;

import com.tmyx.backend.entity.SafeArea;
import com.tmyx.backend.entity.ServiceArea;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.AreaMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/area")
@CrossOrigin
public class AreaController {
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private UserMapper userMapper;

    // 获取所有服务区域
    @GetMapping("/service/all")
    public Result getAllServiceArea() {
        List<ServiceArea> areas = areaMapper.findAllServiceArea();
        return Result.success(areas);
    }

    // 添加服务区域
    @PostMapping("/service/add")
    public String addServiceArea(@RequestBody ServiceArea area) {
        int result = areaMapper.insertServiceArea(area);
        if (result > 0) {
            return "添加成功";
        } else {
            return "添加失败";
        }
    }

    // 删除服务区域
    @DeleteMapping("/service/delete/{id}")
    public String removeServiceArea(@PathVariable Integer id) {
        int result = areaMapper.deleteServiceArea(id);
        if (result > 0) {
            return "删除成功";
        } else {
            return "删除失败";
        }
    }

    // 获取所有安全区域
    @GetMapping("/safe/all")
    public Result getAllSafeArea(@RequestAttribute Integer userId) {
        // 获取当前用户信息
        User currentUser = userMapper.findById(userId);
        // 判断当前用户身份
        if (currentUser.getRole() == 3) {
            // 如果当前用户是老人
            List<SafeArea> areas = areaMapper.findSafeAreaByElderId(userId);
            return Result.success(areas);
        } else {
            // 如果当前用户是家属
            List<SafeArea> areas = areaMapper.findSafeAreaByUserId(userId);
            return Result.success(areas);
        }
    }

    // 添加服务区域
    @PostMapping("/safe/add")
    public String addSafeArea(@RequestBody SafeArea area) {
        int result = areaMapper.insertSafeArea(area);
        if (result > 0) {
            return "添加成功";
        } else {
            return "添加失败";
        }
    }

    // 删除安全区域
    @DeleteMapping("/safe/delete/{id}")
    public String removeSafeArea(@PathVariable Integer id) {
        int result = areaMapper.deleteSafeArea(id);
        if (result > 0) {
            return "删除成功";
        } else {
            return "删除失败";
        }
    }

}
