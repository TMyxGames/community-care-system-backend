package com.tmyx.backend.controller;


import com.tmyx.backend.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class ParamsController {

    // 接收用户名和密码
    // http://localhost:8081/getPage1?username=tmyx&password=123456
    @RequestMapping(value = "/getPage1", method = RequestMethod.GET)
    public String getPage1(String username, String password){
        System.out.println("用户名：" + username);
        System.out.println("密码：" + password);
        return "信息已获取";
    }

    // 接收的参数名称和形参名称不一致
    // http://localhost:8081/getPage2?username=tmyx&password=123456
    @RequestMapping(value = "/getPage2", method = RequestMethod.GET)
    public String getPage2(@RequestParam(value = "username",required = false) String name, String password){
        System.out.println("用户名：" + name);
        System.out.println("密码：" + password);
        return "信息已获取";
    }

    // 传递用户名和密码
    // http://localhost:8081/postPage1
    @RequestMapping(value = "/postPage1", method = RequestMethod.POST)
    public String postPage1(String username, String password){
        System.out.println("用户名：" + username);
        System.out.println("密码：" + password);
        return "信息已传递";
    }

    // 使用类传递用户名和密码
    // http://localhost:8081/postPage2
    @RequestMapping(value = "/postPage2", method = RequestMethod.POST)
    public String postPage2(User user){
        System.out.println(user);
        return "信息已传递";
    }

    //通配符
    // http://localhost:8081/testPage/xxx
    @GetMapping("/testPage/**")
    public String testPage(){
        return "通配符请求";
    }

}
