package com.tmyx.backend.controller;

import com.tmyx.backend.entity.ServiceArea;
import com.tmyx.backend.mapper.ServiceAreaMapper;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/area")
@CrossOrigin
public class ServiceAreaController {
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    // 获取所有服务区域
    @GetMapping("/all")
    public List<ServiceArea> getAllArea() {
        return serviceAreaMapper.findAll();
    }

    // 添加服务区域
    @PostMapping("/add")
    public String addArea(@RequestBody ServiceArea area) {
        int result = serviceAreaMapper.insert(area);
        if (result > 0) {
            return "添加成功";
        } else {
            return "添加失败";
        }
    }

    // 删除服务区域
    @DeleteMapping("/delete/{id}")
    public String removeArea(@PathVariable Integer id) {
        int result = serviceAreaMapper.delete(id);
        if (result > 0) {
            return "删除成功";
        } else {
            return "删除失败";
        }
    }

}
