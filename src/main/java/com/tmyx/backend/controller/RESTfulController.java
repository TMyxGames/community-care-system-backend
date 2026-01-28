package com.tmyx.backend.controller;

import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
public class RESTfulController {

    @Autowired
    private UserMapper userMapper;

    // 根据id查询用户
    // http://localhost:8081/restful1/1
    @GetMapping("/restful1/{id}")
    public String getUserById(@PathVariable int id) {
        User i = userMapper.findById(id);
        if (i != null) {
            return "用户名为：" + i.getUsername();
        } else {
            return "用户不存在";
        }

    }

    // 添加用户
    // http://localhost:8081/restful2
    @PostMapping("/restful2")
    public String saveUser(@RequestBody User user) {
        int i = userMapper.insert(user);
        if (i > 0) {
            return "用户保存成功";
        } else {
            return "用户保存失败";
        }
    }

    // 更新用户
    // http://localhost:8081/restful3
    @PutMapping("/restful3")
    public String updateUser(@RequestBody User user) {
        int i = userMapper.insert(user);
        if (i > 0) {
            return "用户更新成功";
        } else {
            return "用户更新失败";
        }
    }

    // 删除用户
    // http://localhost:8081/restful4/1
    @DeleteMapping("/restful4/{id}")
    public String deleteUser(@PathVariable int id) {
        try {
            int i = userMapper.delete(id);
            if (i > 0) {
                return "用户删除成功";
            } else {
                return "用户删除失败";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage() ;
        }

    }


}
