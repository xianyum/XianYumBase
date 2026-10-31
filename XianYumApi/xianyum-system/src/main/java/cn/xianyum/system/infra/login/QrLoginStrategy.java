package cn.xianyum.system.infra.login;

import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.enums.QrCodeLoginStatus;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.response.QrLoginTicketResponse;
import cn.xianyum.common.utils.RedisUtils;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 二维码登录策略
 * preCheck: 校验Redis中ticket存在性 + 状态为已确认 + 绑定用户名非空
 * doLogin:  获取绑定用户名 → 查询用户 → 构建LoginUser（仅携带id）
 */
@Component
public class QrLoginStrategy extends AbstractLoginStrategy {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.QR;
    }

    @Override
    protected void preCheck(UserLoginRequest request) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), request.getAuthCode());
        if (!redisUtils.hasKey(redisKey)) {
            throw new SoException("二维码过期，请刷新");
        }
        QrLoginTicketResponse qrLoginTicketResponse = JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
        if (!qrLoginTicketResponse.getLoginStatus().equals(QrCodeLoginStatus.CONFIRMED)
                || StrUtil.isBlank(qrLoginTicketResponse.getUserName())) {
            throw new SoException("二维码过期，请刷新");
        }
    }

    @Override
    protected LoginUser doLogin(UserLoginRequest request) {
        String redisKey = String.format(RedisKeyEnum.TOKEN_QR_TICKET.getKey(), request.getAuthCode());
        QrLoginTicketResponse qrLoginTicketResponse = JSONObject.parseObject(redisUtils.getString(redisKey), QrLoginTicketResponse.class);
        UserEntity userEntity = this.queryUser(qrLoginTicketResponse.getUserName());
        LoginUser loginUserEntity = new LoginUser();
        loginUserEntity.setId(userEntity.getId());
        return loginUserEntity;
    }
}
