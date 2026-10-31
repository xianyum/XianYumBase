package cn.xianyum.system.infra.login;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.common.exception.SoException;
import org.springframework.stereotype.Component;

/**
 * 咸鱼客户端登录策略（预留，暂未实现）
 */
@Component
public class XianYuLoginStrategy extends AbstractLoginStrategy {

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.XIAN_YU;
    }

    @Override
    protected LoginUser doLogin(UserLoginRequest request) {
        throw new SoException("咸鱼客户端登录暂未实现");
    }
}
