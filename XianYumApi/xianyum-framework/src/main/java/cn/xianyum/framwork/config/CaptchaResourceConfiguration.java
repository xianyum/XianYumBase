package cn.xianyum.framwork.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cn.xianyum.common.constant.CaptchaConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.TEMPLATE_ACTIVE_IMAGE_NAME;
import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.TEMPLATE_FIXED_IMAGE_NAME;

/**
 * @author xianyum
 * @date 2026/1/27 20:35
 */
@Configuration
public class CaptchaResourceConfiguration {


    @Bean
    public ResourceStore resourceStore() {
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();

        ResourceMap template1 = new ResourceMap(2);
        // 滑块轮廓图片
        template1.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(CaptchaConstant.RESOURCE_TYPE_CLASSPATH, CaptchaConstant.SLIDER_TEMPLATE_1_ACTIVE_PATH));
        // 滑块凹槽图片
        template1.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(CaptchaConstant.RESOURCE_TYPE_CLASSPATH, CaptchaConstant.SLIDER_TEMPLATE_1_FIXED_PATH));

        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template1);
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource(CaptchaConstant.RESOURCE_TYPE_URL, CaptchaConstant.CAPTCHA_DEFAULT_BACKGROUND_URL));
        resourceStore.addResource(CaptchaTypeConstant.CONCAT, new Resource(CaptchaConstant.RESOURCE_TYPE_URL, CaptchaConstant.CAPTCHA_DEFAULT_BACKGROUND_URL));
        resourceStore.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(CaptchaConstant.RESOURCE_TYPE_URL, CaptchaConstant.CAPTCHA_DEFAULT_BACKGROUND_URL));
        return resourceStore;
    }
}
