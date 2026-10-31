package cn.xianyum.system.infra.login;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.system.entity.request.UserLoginRequest;
import org.springframework.stereotype.Component;

/**
 * QQ授权登录策略
 * doLogin: 通过QQ开放平台获取绑定用户
 */
@Component
public class QqLoginStrategy extends AbstractLoginStrategy {

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.QQ;
    }

    @Override
    protected LoginUser doLogin(UserLoginRequest request) {
        LoginUser loginUserEntity = userService.getUserByQq(request);
        return loginUserEntity;
    }
}
