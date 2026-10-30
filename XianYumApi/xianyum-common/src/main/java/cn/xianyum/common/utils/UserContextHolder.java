package cn.xianyum.common.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.entity.LoginUser;
import java.util.Objects;

public class UserContextHolder {


    /**
     * 从当前线程获取用户
     * @return
     */
    public static LoginUser getLoginUser() {
        Object o = SaHolder.getStorage().get(Constants.THREAD_LOGIN_USER);
        return o == null ? null : (LoginUser) o;
    }

    /**
     * 给当前线程设置用户信息
     * @return
     */
    public static void setLoginUser(String userId) {
        LoginUser loginUser = UserCacheHelper.selectUserById(userId);
        if(Objects.isNull(loginUser)){
            return;
        }
        SaHolder.getStorage().set(Constants.THREAD_LOGIN_USER,loginUser);
    }

    /**
     * 给当前线程设置用户信息
     * @return
     */
    public static void setLoginUser(LoginUser loginUser) {
        SaHolder.getStorage().set(Constants.THREAD_LOGIN_USER,loginUser);
    }

    /**
     * 判断是不是管理员
     * @return
     */
    public static boolean isSupperAdminAuth() {
        LoginUser userEntity = getLoginUser();
        return (Objects.nonNull(userEntity) && (Constants.USER_ADMIN_ACCOUNT.equals(userEntity.getUsername())
                || userEntity.getRoles().stream().anyMatch(item -> Constants.USER_ADMIN_ACCOUNT.equals(item))));
    }


    /**
     * 判断是不是管理员
     * @return
     */
    public static boolean isSupperAdminAuth(LoginUser loginUser) {
        return (Objects.nonNull(loginUser) && (Constants.USER_ADMIN_ACCOUNT.equals(loginUser.getUsername())
                || loginUser.getRoles().stream().anyMatch(item -> Constants.USER_ADMIN_ACCOUNT.equals(item))));
    }
}
