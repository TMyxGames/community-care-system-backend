package com.tmyx.backend.controller;

import com.tmyx.backend.dto.UserResetPwdDto;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.dto.UserBindDto;
import com.tmyx.backend.dto.UserLoginDto;
import com.tmyx.backend.dto.UserRegiDto;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.MailService;
import com.tmyx.backend.service.SessionService;
import com.tmyx.backend.service.UserService;
import com.tmyx.backend.util.FileUtil;
import com.tmyx.backend.util.JwtUtil;
import com.tmyx.backend.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private SessionService sessionService;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 未来需要更改为相对路径
    @Value("${file.upload-path}")
    private String baseUploadPath;

    // 发送注册验证码
    @PostMapping("/sendRegiCaptcha")
    public Result sendRegiCaptcha(@RequestParam String email) {
        // 生成验证码
        String captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        // 保存验证码到redis
        redisTemplate.opsForValue().set("CAPTCHA:regi:" + email, captcha, 5, TimeUnit.MINUTES);
        // 发送邮件
        mailService.sendRegiCaptchaMail(email, captcha);
        // 返回结果
        return Result.success();
    }

    // 用户注册
    @PostMapping("/register")
    public Result register(@RequestBody UserRegiDto regiDto) {
        // 从DTO中获取注册信息
        String username = regiDto.getUsername();
        String email = regiDto.getEmail();
        String userInputCode = regiDto.getCaptcha();
        Integer role = regiDto.getRole() != null ? regiDto.getRole() : 0;
        // 从redis中获取验证码
        String realCode = redisTemplate.opsForValue().get("CAPTCHA:regi:" + email);
        // 检查验证码是否有效
        if (realCode == null) {
            return Result.error("验证码已失效，请重新获取");
        }
        // 检查验证码是否正确
        if (!realCode.equals(userInputCode)) {
            return Result.error("验证码错误");
        }
        // 检查用户是否存在
        if (userMapper.findByEmail(email) != null) {
            return Result.error("该用户已注册");
        }
        // 将明文密码加密
        String encodedPassword = passwordEncoder.encode(regiDto.getPassword());
        // 构造用户信息并保存到数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        user.setRole(role);
        userMapper.insert(user);
        // 初始化站内信会话
        sessionService.initDefaultSessions(user.getId());
        // 初始化默认头像
        userService.setDefaultAvatar(user.getId());
        // 删除redis中的验证码
        redisTemplate.delete("CAPTCHA:regi:" + email);
        // 返回结果
        return Result.success("注册成功", null);
    }

    // 用户登录
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDto loginDto) {
        // 通过邮箱获取用户信息
        User user = userMapper.findByEmail(loginDto.getEmail());
        // 检查用户是否存在
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        // 校验密码
        String dbPassword = user.getPassword();
        String rawPassword = loginDto.getPassword();
        boolean loginSuccess = false;
        if (dbPassword != null && dbPassword.startsWith("$2a$")) {
            // 尝试哈希匹配
            if (passwordEncoder.matches(rawPassword, dbPassword)) {
                loginSuccess = true;
            }
        } else {
            // 如果不是哈希值，则进行明文匹配（迁移老用户的明文密码）
            if (rawPassword.equals(dbPassword)) {
                loginSuccess = true;
                // 将明文密码进行哈希加密
                String encodedPassword = passwordEncoder.encode(rawPassword);
                // 将加密后的密码更新回数据库
                userMapper.updatePassword(user.getId(), encodedPassword);
                System.out.println("用户 " + user.getEmail() + " 的密码已自动升级为BCrypt加密存储");
            }
        }
        // 如果两种方式都匹配失败，返回401
        if (!loginSuccess) {
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
                // 必须与保存的身份完全一致
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
        return Result.success("登录成功", responseData);
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
            return Result.success("更新用户信息成功", updatedUser);
        }
        return Result.error("更新用户信息失败");
    }

    // 修改密码（已登录的情况）
    @PutMapping("/password")
    public Result updatePassword(@RequestBody Map<String, String> params, @RequestAttribute Integer userId) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        // 查询用户信息，检查旧密码是否正确
        User user = userMapper.findById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error(401, "旧密码错误");
        }
        // 检查新密码是否与旧密码重复
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            return Result.error(401, "新密码不能与旧密码相同");
        }
        // 将密码进行加密并保存到数据库
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
        return Result.success("密码修改成功", null);
    }

    // 发送重置密码验证码（忘记密码）
    @PostMapping("/sendResetCaptcha")
    public Result sendResetCaptcha(@RequestParam String email) {
        // 生成验证码
        String captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        // 保存验证码到redis
        redisTemplate.opsForValue().set("CAPTCHA:reset:" + email, captcha, 5, TimeUnit.MINUTES);
        // 发送邮件
        mailService.sendResetCaptchaMail(email, captcha);
        // 返回结果
        return Result.success();
    }

    // 重置密码
    @PutMapping("/reset")
    public Result resetPassword(@RequestBody UserResetPwdDto resetDto) {
        // 从DTO中获取用户输入的邮箱和验证码
        String email = resetDto.getEmail();
        String userInputCode = resetDto.getCaptcha();
        // 从redis中获取保存的验证码
        String realCode = redisTemplate.opsForValue().get("CAPTCHA:reset:" + email);
        // 检查用户是否存在
        if (userMapper.findByEmail(resetDto.getEmail()) == null) {
            return Result.error("用户不存在");
        }
        // 根据邮箱获取用户信息
        User user = userMapper.findByEmail(email);
        // 检查验证码是否有效
        if (realCode == null) {
            return Result.error("验证码已失效，请重新获取");
        }
        // 检查验证码是否正确
        if (!realCode.equals(userInputCode)) {
            return Result.error("验证码错误");
        }
        // 将明文密码加密
        String encodedPassword = passwordEncoder.encode(resetDto.getPassword());
        // 保存新密码到数据库
        userMapper.updatePassword(user.getId(), encodedPassword);
        // 删除redis中的验证码
        redisTemplate.delete("CAPTCHA:reset:" + email);
        // 返回结果
        return Result.success("密码重置成功", null);
    }
}
