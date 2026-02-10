package cn.xianyum.system.service;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.response.LoginTokenResponse;

public interface UserTokenService {

    /**
     * 生成token
     * @param user
     * @return
     */
    LoginTokenResponse createToken(LoginUser user);

    /**
     * 退出，修改token值
     */
    void logout();

    /**
     * 刷新redis中user数据
     */
    void refreshUser();

    /**
     * 获取当前登录user
     * @return
     */
    LoginUser getUserSelf();

    void setExtraUserInfo(LoginUser loginUser);

    /**
     * 通过httpRequest获取当前登录用户
     * @return
     */
    LoginUser getLoginUserByHttpRequest();

    /**
     * 发送验证码
     * @param request
     */
    void sendLoginCredentials(UserLoginRequest request);

    /**
     * 发送邮箱验证码
     * @param request
     * @param code
     */
    void sendLoginCredentialsByEmail(UserLoginRequest request, String code);

    /**
     * 登录核心
     * @param request
     * @return
     */
    LoginTokenResponse login(UserLoginRequest request);

    /**
     * 账号密码登录
     * @param request
     * @return
     */
    LoginUser loginPwd(UserLoginRequest request);

    /**
     * 邮箱验证码登录
     * @param request
     * @return
     */
    LoginUser loginEmail(UserLoginRequest request);

    /**
     * QQ授权登录
     * @return
     */
    LoginUser loginByQq(UserLoginRequest request);

    /**
     * 支付宝授权登录
     * @param request
     * @return
     */
    LoginUser loginByZhiFuBao(UserLoginRequest request);
}
