package com.tmyx.backend.controller;

import com.tmyx.backend.entity.User;
import com.tmyx.backend.entity.UserBindDto;
import com.tmyx.backend.entity.UserLoginDto;
import com.tmyx.backend.entity.UserRegiDto;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.MailService;
import com.tmyx.backend.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
    public ResponseEntity<?> sendCaptcha(@RequestParam String email) {
        // 生成验证码
        String captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

        redisTemplate.opsForValue().set("CAPTCHA:" + email, captcha, 5, TimeUnit.MINUTES);
        // 发送邮件
        mailService.sendCaptchaMail(email, captcha);
        // 保存验证码到redis

        return ResponseEntity.ok("验证码发送成功，5分钟内有效");
    }

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegiDto regiDto) {
        String email = regiDto.getEmail();
        String userInputCode = regiDto.getCaptcha();

        // 1. 从 Redis 中获取保存的验证码
        String realCode = redisTemplate.opsForValue().get("CAPTCHA:" + email);

        if (userMapper.findByName(regiDto.getUsername()) != null) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }

        // 2. 校验
        if (realCode == null) {
            return ResponseEntity.badRequest().body("验证码已失效，请重新获取");
        }

        if (!realCode.equals(userInputCode)) {
            return ResponseEntity.badRequest().body("验证码错误");
        }

        // 3. 保存用户信息到数据库
        User user = new User();
        user.setUsername(regiDto.getUsername());
        user.setPassword(regiDto.getPassword());
        user.setEmail(regiDto.getEmail());
        user.setRole(regiDto.getRole() != null ? regiDto.getRole() : 0);
        userMapper.insert(user);

        // 4. 删除 Redis 中的验证码
        redisTemplate.delete("CAPTCHA:" + email);

        return ResponseEntity.ok("注册成功！");
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginDto) {
        User user = userMapper.findByEmail(loginDto.getEmail());

        if (user == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }

        if (!user.getPassword().equals(loginDto.getPassword())) {
            return ResponseEntity.status(401).body("密码错误");
        }

        if (loginDto.getRole() != null) {
            int role = loginDto.getRole();

            if (role != 0 && !user.getRole().equals(role)) {
                String roleName = role == 1 ? "管理员" : "服务人员";
                return ResponseEntity.status(403).body("权限不足：该账号不是" + roleName);
            }
        }

        // 登录成功后返回用户信息，不返回密码
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // 获取绑定数据
    @GetMapping("/bindings")
    public ResponseEntity<?> getBindings(@RequestParam Integer userId) {
        // 调用 UserMapper 中的查询方法
        List<UserBindDto> bindings = userMapper.findBindingsByFollowerId(userId);
        return ResponseEntity.ok(bindings);
    }

    // 用户绑定
    @PostMapping("/bind")
    @Transactional
    public ResponseEntity<?> bindUser(@RequestParam UserBindDto bindDto) {
        int uid1 = bindDto.getFollowerId();
        int uid2 = bindDto.getElderId();

        if (userMapper.countBinding(uid1, uid2) > 0) {
            return ResponseEntity.badRequest().body("已经绑定过了，无需重复绑定");
        }

        userMapper.insertBinding(uid1, uid2, bindDto.getRemark());
        userMapper.insertBinding(uid2, uid1, bindDto.getRemark());
        return ResponseEntity.ok("绑定成功");
    }

    // 用户解绑
    @PostMapping("/unbind")
    @Transactional
    public ResponseEntity<?> unbindUser(@RequestParam Integer followerId,
                                        @RequestParam Integer elderId) {
        userMapper.deleteBinding(followerId, elderId);
        userMapper.deleteBinding(elderId, followerId);
        return ResponseEntity.ok("已解除绑定");
    }

    // 上传头像
    @PostMapping("/upload/avatar")
    public String uploadImg(@RequestParam("file") MultipartFile file,
                            @RequestParam("userId") Integer userId,
                            @RequestParam(value = "oldUrl", required = false) String oldUrl) throws IOException {

        // 保存新文件
        File uploadDir = new File(baseUploadPath, "user/avatars/");
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

        String relativePath = "/user/avatars/" + fileName;
        userMapper.updateAvatar(userId, relativePath);
        return relativePath;
    }

    //更新用户信息
    @PostMapping("/upload/info")
    public ResponseEntity<?> updateInfo(@RequestBody User user) {
        int result = userMapper.updateBaseInfo(user);

        if (result > 0) {
            User updatedUser = userMapper.findById(user.getId());
            updatedUser.setPassword(null);
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.status(500).body("更新用户信息失败");
    }
}
