package cn.xianyum.system.infra.login;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.system.entity.request.UserLoginRequest;

/**
 * 登录策略接口
 * 根据不同登录类型（账号密码、邮箱、QQ、支付宝等）实现各自的认证逻辑
 */
public interface LoginStrategy {

    /**
     * 获取当前策略对应的登录类型
     * @return 登录类型枚举
     */
    LoginTypeEnum getLoginType();

    /**
     * 执行登录认证（模板方法，具体逻辑由抽象基类编排）
     * @param request 登录请求参数
     * @return 登录成功后的用户信息
     */
    LoginUser login(UserLoginRequest request);
}
