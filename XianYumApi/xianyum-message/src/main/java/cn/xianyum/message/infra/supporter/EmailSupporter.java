package cn.xianyum.message.infra.supporter;

import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.message.entity.po.MessageConfigEmailEntity;
import cn.xianyum.message.entity.po.MessageContent;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.infra.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 邮箱支持类
 * @author zhangwei
 * @date 2021/11/21 14:55
 */
@Slf4j
@Component
public class EmailSupporter {

    @Autowired
    private TemplateEngine templateEngine;

    private static int EMAIL_PORT = 465;

    public JavaMailSender javaMailSender(MessageConfigEmailEntity messageConfigEmailEntity) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(messageConfigEmailEntity.getEmailSmtp());
        javaMailSender.setPort(EMAIL_PORT);
        javaMailSender.setUsername(messageConfigEmailEntity.getEmailUserName());
        javaMailSender.setPassword(messageConfigEmailEntity.getEmailUserPassword());
        javaMailSender.setDefaultEncoding("UTF-8");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.smtp.ssl.enable", "true");
        properties.setProperty("mail.smtp.socketFactory.port", String.valueOf(EMAIL_PORT));
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }

    /**
     *
     * @param to 发给谁
     * @param subject 标题
     * @param emailContent 邮件内容
     */
    public void sendContentMail(MessageConfigEmailEntity messageConfigEmailEntity, String to, String subject, String emailContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(messageConfigEmailEntity.getEmailUserName());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(emailContent);
            javaMailSender(messageConfigEmailEntity).send(message);
        } catch (Exception e) {
            log.error("发送邮件失败,{}",e);
        }
    }

    /**
     *
     * @param to 发给谁
     * @param subject 标题
     * @param htmlPath html路径
     * @param context 路径
     */
    public String sendHtmlEmail(MessageConfigEmailEntity messageConfigEmailEntity,String to, String subject, String htmlPath, Context context) {

        JavaMailSender javaMailSender = javaMailSender(messageConfigEmailEntity);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            String emailContent = templateEngine.process(htmlPath,context);
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(messageConfigEmailEntity.getEmailUserName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
            javaMailSender.send(message);
            return "SUCCESS";
        } catch (MessagingException e) {
            log.error("发送邮件失败,{}",e);
            return e.getMessage();
        }
    }

    /**
     * 发送html标签
     * @param messageConfigEmailEntity
     * @param to
     * @param subject
     * @param emailContent
     */
    public String sendHtmlContent(MessageConfigEmailEntity messageConfigEmailEntity,String to, String subject,String emailContent) {

        JavaMailSender javaMailSender = javaMailSender(messageConfigEmailEntity);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(messageConfigEmailEntity.getEmailUserName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
            javaMailSender.send(message);
            return "SUCCESS";
        } catch (MessagingException e) {
            log.error("发送邮件失败,{}",e);
            return e.getMessage();
        }
    }

    /**
     * 构建消息体
     * @param messageSender
     * @return
     */
    public String generateMessageContent(MessageSenderEntity messageSender){
        StringBuffer sb = new StringBuffer();
        if(StringUtil.isNotEmpty(messageSender.getContent())){
            sb.append("<div><p>");
            sb.append(messageSender.getContent());
            sb.append("</p></div>");
        }else{
            List<MessageContent> messageContents = messageSender.getMessageContents();
            if(messageContents != null && messageContents.size() > 0){
                for(MessageContent item : messageContents){
                    sb.append("<div><p>");
                    sb.append(item.getLabel());
                    sb.append("<b><span>");
                    sb.append(item.getValue());
                    sb.append("</span></b></p></div>");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 构建消息体
     * @param messageSender
     * @return
     */
    public List<String> generateMessageContentList(MessageSenderEntity messageSender){
        List<String> contentList = new ArrayList<>();
        if(StringUtil.isNotEmpty(messageSender.getContent())){
            contentList.add(messageSender.getContent());
        }else{
            List<MessageContent> messageContents = messageSender.getMessageContents();
            if(messageContents != null && messageContents.size() > 0){
                for(MessageContent item : messageContents){
                    contentList.add(item.getLabel()+item.getValue());
                }
            }
        }
        return contentList;
    }

}
