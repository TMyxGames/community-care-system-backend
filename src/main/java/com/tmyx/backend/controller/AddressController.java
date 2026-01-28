package com.tmyx.backend.controller;

import com.tmyx.backend.entity.Address;
import com.tmyx.backend.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@CrossOrigin
public class AddressController {
    @Autowired
    private AddressMapper addressMapper;

    // 添加地址
    @PostMapping("/add")
    public String addAddress(@RequestBody Address address) {
        System.out.println("准备存入数据库的对象: " + address.toString());
        if (address.getUserId() == null || address.getArea() == null) {
            return "添加失败：用户信息或区域不能为空";
        }

        int result = addressMapper.insert(address);
        System.out.println("数据库受影响行数: " + result);
        return result > 0 ? "添加成功" : "添加失败";
    }

    // 获取地址
    @GetMapping("/get")
    public ResponseEntity<?> getAddress(@RequestParam Integer userId) {
        // 1. 基础校验
        if (userId == null) {
            return ResponseEntity.badRequest().body("userId 不能为空");
        }

        try {
            List<Address> list = addressMapper.findByUserId(userId);

            // 2. 逻辑判断：如果没有地址，是返回 200 空列表，还是返回 404？
            // 智慧养老系统建议：即使没找到也返回 200 和空列表，方便前端直接渲染 v-for
            return ResponseEntity.ok(list);

        } catch (Exception e) {
            // 3. 异常处理：返回 500 错误
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    // 更新地址
    @PutMapping("/update")
    public String updateAddress(@RequestBody Address address) {
        if (address.getId() == null) {
            return "更新失败：地址ID不能为空";
        }

        int result = addressMapper.update(address);
        return result > 0 ? "更新成功" : "更新失败";
    }

    // 删除地址
    @DeleteMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Integer id) {
        int result = addressMapper.delete(id);
        return result > 0 ? "删除成功" : "删除失败";
    }

    // 设置默认地址
    @PutMapping("/setDefault")
    public String setDefault(@RequestParam Integer id, @RequestParam Integer userId) {
        try {
            // 先将用户所有地址都设为非默认
            addressMapper.resetDefaultByUserId(userId);
            // 设置新的默认地址
            int result = addressMapper.setDefaultById(id);
            return result > 0 ? "设置成功" : "设置失败";
        } catch (Exception e) {
            return "设置失败：" + e.getMessage();
        }
    }
}
