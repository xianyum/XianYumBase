package com.base.service.impl;

import com.base.service.iservice.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author zhangwei
 * @date 2019/3/18 15:50
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender; //读取配置文件中的参数

    @Override
    public void sendHtmlEmail(String to, String subject, String emailContent) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error("发送邮件失败:"+to);
        }
    }

    @Override
    public void sendContentMail(String to, String subject, String emailContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(sender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(emailContent);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送邮件失败:"+to);
        }
    }
}
