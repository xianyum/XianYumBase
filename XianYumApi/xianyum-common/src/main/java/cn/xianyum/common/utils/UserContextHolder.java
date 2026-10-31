package cn.xianyum.common.utils;

import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.entity.LoginUser;
import java.util.Objects;

public class UserContextHolder {

    /**
     * 线程本地存储用户信息
     */
    private static final ThreadLocal<LoginUser> USER_THREAD_LOCAL = new ThreadLocal<>();


    /**
     * 从当前线程获取用户
     * @return
     */
    public static LoginUser getLoginUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 给当前线程设置用户信息
     * @return
     */
    public static void setLoginUser(String userId) {
        if(Objects.isNull(userId)){
            return;
        }
        LoginUser loginUser = UserCacheHelper.selectUserById(userId);
        if(Objects.isNull(loginUser)){
            return;
        }
        USER_THREAD_LOCAL.set(loginUser);
    }

    /**
     * 给当前线程设置用户信息，并设置token数据
     * @return
     */
    public static void setLoginUser(String userId,String token) {
        if(Objects.isNull(userId)){
            return;
        }
        LoginUser loginUser = UserCacheHelper.selectUserById(userId);
        if(Objects.isNull(loginUser)){
            return;
        }
        loginUser.setToken(token);
        USER_THREAD_LOCAL.set(loginUser);
    }


    /**
     * 给当前线程设置用户信息
     * @return
     */
    public static void setLoginUser(LoginUser loginUser) {
        USER_THREAD_LOCAL.set(loginUser);
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

    /**
     * 清除当前线程的用户信息
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
