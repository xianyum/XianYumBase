package cn.xianyum.system.service;


import java.awt.image.BufferedImage;

public interface CaptchaService {

    /**
     * 获取图片
     * @param uuid
     * @return
     */
    BufferedImage getCaptcha(String uuid);

    /**
     * 验证码效验
     * @param uuid  uuid
     * @param code  验证码
     * @return  true：成功  false：失败
     */
    boolean validate(String uuid, String code);

}
