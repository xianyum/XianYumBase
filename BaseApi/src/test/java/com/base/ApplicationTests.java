package com.base;

import com.alibaba.fastjson.JSONObject;
import com.base.common.utils.HttpUtils;
import com.base.common.utils.JwtUtils;
import com.base.common.utils.RedisUtils;
import com.base.common.utils.UUIDUtils;
import com.base.config.PhoneConfig;
import com.base.dao.UserMapper;
import com.base.service.iservice.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplicationTests {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PhoneConfig phoneConfig;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void testRegx(){
        Context context = new Context();
        context.setVariable("emailCode", UUIDUtils.getCodeChar(6));
        String emailContent =templateEngine.process("codeMail",context);
        emailService.sendHtmlEmail("80616059@qq.com","注册提醒",emailContent);
    }
    @Test
    public void testSendEmail(){
        //https://office.ucpaas.com/controlHtmls/pages/sms/sms_product.html
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sid",phoneConfig.getSid());
        jsonObject.put("token",phoneConfig.getToken());
        jsonObject.put("appid",phoneConfig.getAppid());
        jsonObject.put("param",UUIDUtils.getRandomNumber(6));
        jsonObject.put("templateid",phoneConfig.getTemplateid());
        jsonObject.put("mobile","17791291739");
        HttpUtils.sendPostJson("https://open.ucpaas.com/ol/sms/sendsms",jsonObject.toString());
    }
    @Test
    public void test(){
        redisUtils.set("cc","13131");
        String c = (String)redisUtils.get("cc");
        System.out.println("取出的值是:"+c);
    }
}

