package com.base.service.impl;

import com.base.common.exception.SoException;
import com.base.common.utils.RedisUtils;
import com.base.common.utils.StringUtil;
import com.base.common.utils.UUIDUtils;
import com.base.config.PhoneConfig;
import com.base.entity.request.UserRequest;
import com.base.service.iservice.CaptchaService;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.awt.image.BufferedImage;


@Service
@Slf4j
public class CaptchaServiceImpl  implements CaptchaService {

    @Autowired
    private PhoneConfig phoneConfig;

    @Resource(name = "captchaProducerMath")
    private Producer producer;

    @Value("${redis.captcha.expire:60}")
    private Integer expireMinTime;

    @Value("${redis.captcha.prefix:captcha}")
    private String prefix;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public BufferedImage getCaptcha(String uuid) {
        if(StringUtil.isBlank(uuid)){
            throw new SoException("uuid不能为空");
        }
        //生成验证码
        String capText = producer.createText();
        String capStr = capText.substring(0, capText.lastIndexOf("@"));
        String code = capText.substring(capText.lastIndexOf("@") + 1);
        redisUtils.set(prefix+uuid,code,expireMinTime);
        return producer.createImage(capStr);
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
    public void getPhoneCaptcha(UserRequest request) {
        String[] phoneNumbers = {request.getMobile()};
        try {
            String code = UUIDUtils.getRandomNumber(6);
            String[] params = {code,expireMinTime.toString()};
            SmsSingleSender ssender = new SmsSingleSender(phoneConfig.getAppid(), phoneConfig.getAppkey());
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    phoneConfig.getTemplateId(), params, phoneConfig.getSmsSign(), "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            redisUtils.setMin(prefix+request.getUuid(),code,expireMinTime);
        } catch (Exception e) {
            log.error("获取短信验证码出错:"+e.getMessage());
        }
    }
}
