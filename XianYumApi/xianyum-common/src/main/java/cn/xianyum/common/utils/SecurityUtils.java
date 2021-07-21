package cn.xianyum.common.utils;

import cn.xianyum.common.entity.UserTokenEntity;
import com.alibaba.fastjson.JSONObject;

/**
 * 安全服务工具类
 * @author zhangwei
 * @date 2021/7/16 23:29
 */
public class SecurityUtils {

    private static String prefix = PropertiesUtil.getString("redis.token.prefix");

    /**
     * 获取当前登录用户信息
     * @return
     */
    public static UserTokenEntity getLoginUser(){

        String token = HttpContextUtils.getRequestToken();
        String userEntityJson = (String)SpringUtils.getBean(RedisUtils.class).get(prefix+token);
        UserTokenEntity userEntity = JSONObject.parseObject(userEntityJson, UserTokenEntity.class);
        return userEntity;
    }


}
