package cn.xianyum.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.common.entity.LoginUser;
import java.util.*;

/**
 * 用户
 * @author zhangwei
 * @date 2023/11/6 21:40
 */
public class UserCacheHelper {


    /**
     * UserService 全限定类名，用于反射加载（跨模块无法直接依赖）
     */
    private static final String USER_SERVICE = "cn.xianyum.system.service.UserService";

    private static final String SELECT_USER_CACHE_BY_ID = "selectUserCacheById";

    /**
     * 根据用户ID获取用户缓存数据(通过反射调用UserService.selectUserCacheById)
     * @param userId
     * @return
     */
    public static LoginUser selectUserById(String userId){
        if(StrUtil.isEmpty(userId)){
            return null;
        }
        try {
            Class<?> userServiceClass = Class.forName(USER_SERVICE);
            Object userService = SpringUtil.getBean(userServiceClass);
            Object result = userServiceClass.getMethod(SELECT_USER_CACHE_BY_ID, String.class).invoke(userService, userId);
            return (LoginUser) result;
        } catch (Exception e) {
            throw new RuntimeException("调用UserService.selectUserCacheById失败", e);
        }
    }

}
