package cn.xianyum.system.entity.request;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import lombok.Data;

/**
 * @author xianyum
 * @date 2026/1/26 20:46
 */
@Data
public class CheckCaptchaRequest {

    // 验证码id,前端回传的验证码ID
    private String id;

    // 验证码数据,前端回传的验证码轨迹数据
    private ImageCaptchaTrack data;
}
