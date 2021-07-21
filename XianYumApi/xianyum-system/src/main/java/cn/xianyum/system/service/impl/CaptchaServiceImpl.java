package cn.xianyum.system.service.impl;


import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.service.CaptchaService;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.awt.image.BufferedImage;


@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {


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

}
