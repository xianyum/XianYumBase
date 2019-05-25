package com.base.service.iservice;

/**
 * @author zhangwei
 * @date 2019/3/18 15:49
 */
public interface EmailService {

    void sendHtmlEmail(String to,String subject,String emailContent);

    void sendContentMail(String to,String subject,String emailContent);
}
