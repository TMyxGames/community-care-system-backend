package com.tmyx.backend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    //通用发送方法
    private void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    // 邮箱验证码
    public void sendCaptchaMail(String to, String code) {
        // 邮件主题
        String subject = "【注册验证码】社区智慧养老服务系统";
        // 邮件内容
        String content = "您的验证码为：" + code + "，有效期为5分钟，请勿告知他人。";
        sendMail(to, subject, content);
    }

    // 安全告警邮件
    @Async
    public void sendAlarmMail(String to, String username) {
        // 邮件主题
        String subject = "【紧急告警】社区智慧养老服务系统";
        // 邮件内容
        String content = "监测到 [" + username + "] 离开了安全区域！请尽快处理！";
        sendMail(to, subject, content);
    }

    // 返回安全邮件
    @Async
    public void sendBackToSafetyMail(String to, String username) {
        // 邮件主题
        String subject = "【平安提醒】社区智慧养老服务系统";
        // 邮件内容
        String content = "[" + username + "] 已返回安全区域！";
        sendMail(to, subject, content);
    }



}
