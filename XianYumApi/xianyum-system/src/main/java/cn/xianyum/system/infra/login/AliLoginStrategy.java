package cn.xianyum.system.infra.login;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.system.entity.request.UserLoginRequest;
import org.springframework.stereotype.Component;

/**
 * 支付宝授权登录策略
 * doLogin: 通过支付宝授权码获取绑定用户
 */
@Component
public class AliLoginStrategy extends AbstractLoginStrategy {

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.ZHI_FU_BAO;
    }

    @Override
    protected LoginUser doLogin(UserLoginRequest request) {
        LoginUser loginUserEntity = userService.getUserByAli(request.getAuthCode());
        return loginUserEntity;
    }
}
