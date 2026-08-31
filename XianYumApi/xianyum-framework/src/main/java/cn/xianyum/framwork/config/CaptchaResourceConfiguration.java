package cn.xianyum.framwork.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.FontCache;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cn.xianyum.common.constant.CaptchaConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cloud.tianai.captcha.resource.common.model.dto.Resource;

import java.util.Arrays;
import java.util.List;

/**
 * @author xianyum
 * @date 2026/1/27 20:35
 */
@Configuration
public class CaptchaResourceConfiguration {

    @Bean
    public ResourceStore resourceStore() {
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();
        // 需要配置背景图的验证码类型
        List<String> captchaTypes = Arrays.asList(CaptchaTypeConstant.SLIDER, CaptchaTypeConstant.CONCAT, CaptchaTypeConstant.WORD_IMAGE_CLICK, CaptchaTypeConstant.ROTATE);
        for (String type : captchaTypes) {
            for (int i = 1; i <= 10; i++) {
                resourceStore.addResource(type, new Resource(CaptchaConstant.RESOURCE_TYPE_CLASSPATH,String.format(CaptchaConstant.CAPTCHA_DEFAULT_BACKGROUND_URL, i)));
            }
        }
       // 配置字体包
        resourceStore.addResource(FontCache.FONT_TYPE, new Resource(CaptchaConstant.RESOURCE_TYPE_CLASSPATH, CaptchaConstant.FONT_PATH, CaptchaConstant.FONT_TAG));
        return resourceStore;
    }
}
