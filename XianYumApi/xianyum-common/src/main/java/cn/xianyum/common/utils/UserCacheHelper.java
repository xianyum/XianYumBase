package cn.xianyum.common.utils;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.RedisKeyEnum;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 用户
 * @author zhangwei
 * @date 2023/11/6 21:40
 */
@Component
public class UserCacheHelper {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 根据用户ID获取redis缓存中数据
     * @param userId
     * @return
     */
    public LoginUser getUserByIdFromRedis(String userId){
        if(StringUtil.isEmpty(userId)){
            return null;
        }
        String userRedisStr = (String)redisUtils.hGet(RedisKeyEnum.USER_DATA.getKey(), userId);
        if(StringUtil.isNotEmpty(userRedisStr)){
            return JSONObject.parseObject(userRedisStr,LoginUser.class);
        }
        return null;
    }


    /**
     * 根据用户ID获取redis缓存中数据
     * @param userId
     * @return
     */
    public String getUserJsonByIdFromRedis(String userId){
        String userRedisStr = (String)redisUtils.hGet(RedisKeyEnum.USER_DATA.getKey(), userId);
        return userRedisStr;
    }

    /**
     * 从redis中取出所有用户列表
     * @return
     */
    public List<LoginUser> getUserListFromRedis(){
        List<LoginUser> loginUserList = new ArrayList<>();
        // key-->userId  value-->用户json
        Map<String, String> userMap = (Map<String, String>)redisUtils.hGetAll(RedisKeyEnum.USER_DATA.getKey());
        if(Objects.isNull(userMap)){
            return loginUserList;
        }
        Set<String> userIdSet = userMap.keySet();
        for(String userId : userIdSet){
            String userJson = userMap.get(userId);
            loginUserList.add(JSONObject.parseObject(userJson,LoginUser.class));
        }
        return loginUserList;
    }


    /**
     * 从redis中取出所有用户列表
     * @return
     */
    public String getUserJsonArrayFromRedis(){
        List<String> loginUserJson = new ArrayList<>();
        // key-->userId  value-->用户json
        Map<String, String> userMap = (Map<String, String>)redisUtils.hGetAll(RedisKeyEnum.USER_DATA.getKey());
        if(Objects.isNull(userMap)){
            return null;
        }
        Set<String> userIdSet = userMap.keySet();
        for(String userId : userIdSet){
            String userJson = userMap.get(userId);
            loginUserJson.add(userJson);
        }
        return JSONObject.toJSONString(loginUserJson);
    }
}
