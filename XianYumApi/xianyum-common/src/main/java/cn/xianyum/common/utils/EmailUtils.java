package cn.xianyum.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.thymeleaf.context.Context;

/**
 * @author zhangwei
 * @date 2021/6/5 9:07
 */
@Component
@Slf4j
public class EmailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username:'80616059@qq.com'}")
    private String sender;


    /**
     *
     * @param to 发给谁
     * @param subject 标题
     * @param htmlPath html路径
     * @param context 路径
     */
    public void sendHtmlEmail(String to, String subject, String htmlPath, Context context) {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            String emailContent =templateEngine.process(htmlPath,context);
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("发送邮件失败,{}",e);
        }
    }

    /**
     *
     * @param to 发给谁
     * @param subject 标题
     * @param emailContent 邮件内容
     */
    public void sendContentMail(String to, String subject, String emailContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(sender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(emailContent);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("发送邮件失败,{}",e);
        }
    }
}
