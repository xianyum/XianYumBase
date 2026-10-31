package cn.xianyum.system.infra.login;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.service.UserService;
import jakarta.annotation.Resource;

import java.util.Objects;

/**
 * 登录策略抽象基类（Template Method 模式）
 * 固定登录流程骨架：preCheck → doLogin → postProcess
 * 子类按需覆盖钩子方法实现差异化逻辑
 */
public abstract class AbstractLoginStrategy implements LoginStrategy {

    @Resource
    protected UserService userService;

    /**
     * 登录流程骨架（final 禁止子类重写，保证流程一致性）
     * 统一在 doLogin 之后校验账号状态，避免子类遗漏
     *
     * @param request 登录请求
     * @return 登录用户
     */
    @Override
    public final LoginUser login(UserLoginRequest request) {
        this.preCheck(request);
        LoginUser loginUser = this.doLogin(request);
        return this.postProcess(loginUser, request);
    }

    /**
     * 前置校验钩子：参数校验、验证码校验等认证前的检查
     * 默认空实现，子类按需覆盖
     *
     * @param request 登录请求
     */
    protected void preCheck(UserLoginRequest request) {
    }

    /**
     * 核心认证逻辑（必须实现）：查询/获取用户、校验密码或授权等
     *
     * @param request 登录请求
     * @return 认证通过后的 LoginUser
     */
    protected abstract LoginUser doLogin(UserLoginRequest request);

    /**
     * 后置处理钩子：默认校验账号锁定状态 + 设置 loginType 标识
     * 子类可覆盖做额外清理工作（如删验证码），覆盖时需调用 super.postProcess() 以保留状态校验
     *
     * @param loginUser 认证通过的登录用户
     * @param request   登录请求
     * @return 处理后的登录用户
     */
    protected LoginUser postProcess(LoginUser loginUser, UserLoginRequest request) {
        if (loginUser.getStatus() == YesOrNoEnum.NO.getStatus()) {
            throw new SoException("账号已被锁定,请联系管理员");
        }
        loginUser.setLoginType(request.getLoginType().getAccountType());
        return loginUser;
    }

    /**
     * 根据用户名查询用户，不存在则抛出异常
     * @param username 用户名
     * @return 用户实体
     */
    protected UserEntity queryUser(String username) {
        UserEntity userEntity = userService.queryByUserName(username);
        if (Objects.isNull(userEntity)) {
            throw new SoException("用户不存在");
        }
        return userEntity;
    }
}
