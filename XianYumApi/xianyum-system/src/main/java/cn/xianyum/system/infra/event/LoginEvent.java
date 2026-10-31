package cn.xianyum.system.infra.event;

import cn.xianyum.common.enums.LoginEventStatusEnum;

import java.util.Date;

/**
 * 登录事件（record 风格，不可变、线程安全）
 *
 * @param status       事件状态：SUCCESS / FAIL
 * @param username     登录用户名
 * @param ipaddr       登录IP
 * @param loginType    登录来源（账号密码、QQ、支付宝、二维码等）
 * @param loginTime    登录时间
 * @param failReason   失败原因（仅 FAIL 时有值）
 * @param token        登录成功时的会话 token（仅 SUCCESS 时有值）
 * @param userId       登录用户 id（仅 SUCCESS 时有值）
 * @param browser      浏览器（仅 SUCCESS 时有值）
 * @param os           操作系统（仅 SUCCESS 时有值）
 * @param loginLocation 登录地点（仅 SUCCESS 时有值）
 */
public record LoginEvent(
        LoginEventStatusEnum status,
        String username,
        String ipaddr,
        String loginType,
        Date loginTime,
        String failReason,
        String token,
        String userId,
        String browser,
        String os,
        String loginLocation
) {

    public static LoginEvent success(String username, String ipaddr, String loginType,
                                     String token, String userId,
                                     String browser, String os, String loginLocation) {
        return new LoginEvent(
                LoginEventStatusEnum.SUCCESS,
                username, ipaddr, loginType, new Date(),
                null,
                token, userId,
                browser, os, loginLocation
        );
    }

    public static LoginEvent fail(String username, String ipaddr, String loginType, String failReason) {
        return new LoginEvent(
                LoginEventStatusEnum.FAIL,
                username, ipaddr, loginType, new Date(),
                failReason,
                null, null, null, null, null
        );
    }
}
