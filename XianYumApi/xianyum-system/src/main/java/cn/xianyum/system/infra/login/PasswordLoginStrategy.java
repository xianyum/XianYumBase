package cn.xianyum.system.infra.login;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.SecretUtils;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 账号密码登录策略
 * preCheck: 校验账号密码非空 + 滑动验证码
 * doLogin:  查询用户 → 校验密码
 */
@Component
public class PasswordLoginStrategy extends AbstractLoginStrategy {

    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.USER_PASSWORD;
    }

    @Override
    protected void preCheck(UserLoginRequest request) {
        if (StrUtil.isBlank(request.getUsername()) || StrUtil.isBlank(request.getPassword())) {
            throw new SoException("账号密码不能为空");
        }
        if (StrUtil.isBlank(request.getVerifyCode())) {
            throw new SoException("未通过验证码校验");
        }
        boolean valid = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(request.getVerifyCode());
        if (!valid && request.getLoginType().equals(LoginTypeEnum.USER_PASSWORD)) {
            throw new SoException("验证码错误");
        }
    }

    @Override
    protected LoginUser doLogin(UserLoginRequest request) {
        UserEntity userEntity = this.queryUser(request.getUsername());
        if (!SecretUtils.matchesPassword(request.getPassword(), userEntity.getPassword())) {
            throw new SoException("密码不正确");
        }
        LoginUser loginUser = BeanUtil.toBean(userEntity, LoginUser.class);
        return loginUser;
    }
}
