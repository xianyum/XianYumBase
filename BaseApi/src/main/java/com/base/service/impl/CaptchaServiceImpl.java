package com.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.base.common.utils.*;
import com.google.code.kaptcha.Producer;
import com.base.common.exception.SoException;
import com.base.config.PhoneConfig;
import com.base.entity.request.LoginPhoneRequest;
import com.base.entity.request.RegisterInfoRequest;
import com.base.service.iservice.CaptchaService;
import com.base.service.iservice.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.awt.image.BufferedImage;


@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
    @Autowired
    private Producer producer;

    @Value("${redis.captcha.expire:60}")
    private Integer expireMinTime;

    @Value("${redis.captcha.prefix:captcha}")
    private String prefix;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PhoneConfig phoneConfig;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public BufferedImage getCaptcha(String uuid) {
        if(StringUtils.isBlank(uuid)){
            throw new SoException("uuid不能为空");
        }
        //生成文字验证码
        String code = producer.createText();
        redisUtils.setMin(prefix+uuid,code,expireMinTime);
        return producer.createImage(code);
    }



    @Override
    public boolean validate(String uuid, String code) {
        String captchaCode = (String)redisUtils.get(prefix+uuid);
        if(StringUtil.isEmpty(captchaCode)){
            return false;
        }else{
            redisUtils.del(prefix+uuid);
            if(code.equals(captchaCode)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void createRegisterCode(RegisterInfoRequest request) {
        String code = UUIDUtils.getCodeChar(6);
        log.info("获取注册邮箱{}的验证码为{}",request.getEmail(),code);
        redisUtils.setMin(prefix+request.getUuid(),code,expireMinTime);
        Context context = new Context();
        context.setVariable("emailCode", code);
        String emailContent =templateEngine.process("codeMail",context);
        emailService.sendHtmlEmail(request.getEmail(),"注册提醒",emailContent);
    }

    @Override
    public boolean registerValidate(String uuid, String code) {
        String captchaCode = (String)redisUtils.get(prefix+uuid);
        if(StringUtil.isEmpty(captchaCode)){
            return false;
        }else{
            redisUtils.del(prefix+uuid);
            if(code.equals(captchaCode)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void createPhoneCode(LoginPhoneRequest request) {
        if(StringUtil.isEmpty(request.getPhone())){
            throw new SoException("手机号不能为空");
        }
        JSONObject jsonObject = new JSONObject();
        String code = UUIDUtils.getRandomNumber(6);
        jsonObject.put("sid",phoneConfig.getSid());
        jsonObject.put("token",phoneConfig.getToken());
        jsonObject.put("appid",phoneConfig.getAppid());
        jsonObject.put("param",code);
        jsonObject.put("templateid",phoneConfig.getTemplateid());
        jsonObject.put("mobile",request.getPhone());
        HttpUtils.sendPostJson("https://open.ucpaas.com/ol/sms/sendsms",jsonObject.toString());
        redisUtils.setMin(prefix+request.getUuid(),code,expireMinTime);
    }
}
