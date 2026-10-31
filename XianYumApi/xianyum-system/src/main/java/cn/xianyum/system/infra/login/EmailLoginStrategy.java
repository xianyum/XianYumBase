package cn.xianyum.system.infra.login;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 邮箱验证码登录策略
 * preCheck: 校验邮箱非空 + Redis验证码存在性 + 验证码比对
 * doLogin:  查询用户 → 构建 LoginUser
 * postProcess: 登录成功后清除Redis验证码（覆盖默认实现）
 */
@Component
public class EmailLoginStrategy extends AbstractLoginStrategy {

    private static final String REDIS_KEY_TEMPLATE = RedisKeyEnum.TOKEN_CREDENTIALS_PREFIX.getKey();

    @Resource
    private RedisUtils redisUtils;

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.EMAIL;
    }

    @Override
    protected void preCheck(UserLoginRequest request) {
        if (StrUtil.isBlank(request.getUsername())) {
            throw new SoException("邮箱账号不能为空");
        }
        String redisKey = String.format(REDIS_KEY_TEMPLATE, request.getUsername());
        if (!redisUtils.hasKey(redisKey)) {
            throw new SoException("验证码已过期");
        }
        if (StrUtil.isBlank(request.getCode()) || !request.getCode().equals(redisUtils.getString(redisKey))) {
            throw new SoException("验证码错误");
        }
    }

    @Override
    protected LoginUser doLogin(UserLoginRequest request) {
        UserEntity userEntity = this.queryUser(request.getUsername());
        LoginUser loginUser = BeanUtil.toBean(userEntity, LoginUser.class);
        return loginUser;
    }

    @Override
    protected LoginUser postProcess(LoginUser loginUser, UserLoginRequest request) {
        String redisKey = String.format(REDIS_KEY_TEMPLATE, request.getUsername());
        redisUtils.del(redisKey);
        return super.postProcess(loginUser, request);
    }
}
