package com.tmyx.backend.controller;

import com.tmyx.backend.entity.User;
import com.tmyx.backend.dto.UserBindDto;
import com.tmyx.backend.dto.UserLoginDto;
import com.tmyx.backend.dto.UserRegiDto;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.MailService;
import com.tmyx.backend.util.FileUtil;
import com.tmyx.backend.util.JwtUtil;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailService mailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 未来需要更改为相对路径
    @Value("${file.upload-path}")
    private String baseUploadPath;

    // 发送验证码
    @PostMapping("/sendCaptcha")
    public Result sendCaptcha(@RequestParam String email) {
        // 生成验证码
        String captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        // 保存验证码到redis
        redisTemplate.opsForValue().set("CAPTCHA:" + email, captcha, 5, TimeUnit.MINUTES);
        // 发送邮件
        mailService.sendCaptchaMail(email, captcha);
        // 返回结果
        return Result.success();
    }

    // 用户注册
    @PostMapping("/register")
    public Result register(@RequestBody UserRegiDto regiDto) {
        String email = regiDto.getEmail();
        String userInputCode = regiDto.getCaptcha();
        // 从redis中获取保存的验证码
        String realCode = redisTemplate.opsForValue().get("CAPTCHA:" + email);
        // 校验用户是否存在（未来封装到Service里）
        if (userMapper.findByName(regiDto.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        // 校验验证码是否有效
        if (realCode == null) {
            return Result.error("验证码已失效，请重新获取");
        }
        // 校验验证码是否正确
        if (!realCode.equals(userInputCode)) {
            return Result.error("验证码错误");
        }
        // 保存用户信息到数据库
        User user = new User();
        user.setUsername(regiDto.getUsername());
        user.setPassword(regiDto.getPassword());
        user.setEmail(regiDto.getEmail());
        user.setRole(regiDto.getRole() != null ? regiDto.getRole() : 0);
        userMapper.insert(user);
        // 删除 Redis 中的验证码
        redisTemplate.delete("CAPTCHA:" + email);
        // 返回结果
        return Result.success();
    }

    // 用户登录
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDto loginDto) {
        User user = userMapper.findByEmail(loginDto.getEmail());
        // 校验用户是否存在
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        // 校验密码
        if (!user.getPassword().equals(loginDto.getPassword())) {
            return Result.error(401, "密码错误");
        }
        // 校验用户身份
        if (loginDto.getRole() != null) {
            Integer requestedRole = loginDto.getRole();
            Integer actualRole = user.getRole();
            // 尝试以家属身份登录（role=0）
            if (requestedRole == 0) {
                // 只有老人不能以家属身份登录（role=3）
                if (actualRole == 3) {
                    return Result.error(403, "该账号身份为“老人”，请选择“老人”身份登录");
                }
            } else {
                // 必须与数据库身份完全一致
                if (!actualRole.equals(requestedRole)) {
                    String roleName = "";
                    switch (requestedRole) {
                        case 1: roleName = "管理员"; break;
                        case 2: roleName = "服务人员"; break;
                        case 3: roleName = "老人"; break;
                    }
                    return Result.error(403, "权限不足：该账号不是" + roleName);
                }
            }
        }
        // 登录成功，生成JWT Token
        String token = JwtUtil.createToken(user.getId());
        // 返回数据，不返回密码
        user.setPassword(null);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userInfo", user);
        return Result.success(responseData);
    }

    // 获取绑定数据
    @GetMapping("/bindings")
    public Result getBindings(@RequestAttribute Integer userId) {
        // 获取当前用户信息
        User currentUser = userMapper.findById(userId);
        List<UserBindDto> bindings;
        // 根据用户身份获取数据
        if (currentUser.getRole() == 3) {
            // 如果当前用户是老人，则获取老人绑定的家属列表
            bindings = userMapper.findFollowersByElderId(userId);
        } else {
            // 如果当前用户是家属，则获取家属绑定的老人列表
            bindings = userMapper.findEldersByFollowerId(userId);
        }
        return Result.success(bindings);
    }

    // 上传头像
    @PostMapping("/upload/avatar")
    public String uploadImg(@RequestParam("file") MultipartFile file,
                            @RequestParam("userId") Integer userId,
                            @RequestParam(value = "oldUrl", required = false) String oldUrl) throws IOException {

        // 保存新文件
        String subPath = "user/avatars/";
        File uploadDir = new File(baseUploadPath, subPath);
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

        String relativePath = "/files/" + subPath + fileName;
        userMapper.updateAvatar(userId, relativePath);
        return relativePath;
    }

    //更新用户信息
    @PostMapping("/upload/info")
    public Result updateInfo(@RequestBody User user) {
        int result = userMapper.updateBaseInfo(user);

        if (result > 0) {
            User updatedUser = userMapper.findById(user.getId());
            updatedUser.setPassword(null);
            return Result.success(updatedUser);
        }
        return Result.error("更新用户信息失败");
    }
}
