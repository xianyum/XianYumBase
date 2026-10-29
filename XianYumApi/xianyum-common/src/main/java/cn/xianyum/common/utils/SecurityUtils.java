package cn.xianyum.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.RedisKeyEnum;
import com.alibaba.fastjson2.JSONObject;

import java.util.Objects;

public class SecurityUtils {

    private static final ThreadLocal<LoginUser> THREAD_LOCAL_USER = new ThreadLocal<>();

    public static LoginUser getLoginUser() {
        LoginUser threadLocalUser = THREAD_LOCAL_USER.get();
        if (Objects.nonNull(threadLocalUser)) {
            return threadLocalUser;
        }
        String token = HttpContextUtils.getRequestToken();
        if (StrUtil.isBlank(token)) {
            return null;
        }
        String userJson = (String) SpringUtil.getBean(RedisUtils.class).get(RedisKeyEnum.TOKEN_PREFIX.getKey() + token);
        if (StrUtil.isBlank(userJson)) {
            return null;
        }
        return JSONObject.parseObject(userJson, LoginUser.class);
    }

    public static void setLoginUser(LoginUser loginUser) {
        if (Objects.nonNull(loginUser)) {
            THREAD_LOCAL_USER.set(loginUser);
        }
    }

    public static void clearLoginUser() {
        THREAD_LOCAL_USER.remove();
    }

    public static boolean isSupperAdminAuth() {
        LoginUser userEntity = getLoginUser();
        return (Objects.nonNull(userEntity) && (Constants.USER_ADMIN_ACCOUNT.equals(userEntity.getUsername())
                || userEntity.getRoles().stream().anyMatch(item -> Constants.USER_ADMIN_ACCOUNT.equals(item))));
    }
}
