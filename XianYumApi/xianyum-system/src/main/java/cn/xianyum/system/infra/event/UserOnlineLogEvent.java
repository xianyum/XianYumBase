package cn.xianyum.system.infra.event;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.LoginEventStatusEnum;

/**
 * 用户在线状态变更事件
 * - LOGIN  ：用户登录成功 → 写入在线用户表
 * - LOGOUT ：用户主动登出 → 从在线用户表删除
 *
 * @param status   事件类型：LOGIN / LOGOUT
 * @param loginUser 登录用户信息
 */
public record UserOnlineLogEvent(LoginEventStatusEnum status, LoginUser loginUser) {

    /**
     * 构建登录事件
     */
    public static UserOnlineLogEvent login(LoginUser loginUser) {
        return new UserOnlineLogEvent(LoginEventStatusEnum.LOGIN, loginUser);
    }


    /**
     * 构建登出事件
     */
    public static UserOnlineLogEvent logout() {
        return new UserOnlineLogEvent(LoginEventStatusEnum.LOGOUT,null);
    }
}
