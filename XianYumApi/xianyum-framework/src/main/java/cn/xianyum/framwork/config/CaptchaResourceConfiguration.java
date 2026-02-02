package cn.xianyum.framwork.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.FontCache;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cn.xianyum.common.constant.CaptchaConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cloud.tianai.captcha.resource.common.model.dto.Resource;

/**
 * @author xianyum
 * @date 2026/1/27 20:35
 */
@Configuration
public class CaptchaResourceConfiguration {

    @Bean
    public ResourceStore resourceStore() {
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource(CaptchaConstant.RESOURCE_TYPE_URL, CaptchaConstant.CAPTCHA_DEFAULT_BACKGROUND_URL));
        resourceStore.addResource(CaptchaTypeConstant.CONCAT, new Resource(CaptchaConstant.RESOURCE_TYPE_URL, CaptchaConstant.CAPTCHA_DEFAULT_BACKGROUND_URL));
        resourceStore.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(CaptchaConstant.RESOURCE_TYPE_URL, CaptchaConstant.CAPTCHA_DEFAULT_BACKGROUND_URL));
        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource(CaptchaConstant.RESOURCE_TYPE_URL, CaptchaConstant.CAPTCHA_DEFAULT_BACKGROUND_URL));
        // 配置字体包
        resourceStore.addResource(FontCache.FONT_TYPE, new Resource(CaptchaConstant.RESOURCE_TYPE_CLASSPATH, CaptchaConstant.FONT_PATH, CaptchaConstant.FONT_TAG));
        return resourceStore;
    }
}
