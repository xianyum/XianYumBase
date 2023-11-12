package cn.xianyum.system.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.entity.po.UserOnlineEntity;
import cn.xianyum.system.entity.request.UserOnlineRequest;
import cn.xianyum.system.entity.response.UserOnlineResponse;
import cn.xianyum.system.service.UserOnlineService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2021/1/28 22:03
 */
@Service
@Slf4j
public class UserOnlineServiceImpl implements UserOnlineService {

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.token.prefix:token}")
    private String prefix;

    @Override
    public PageResponse<UserOnlineResponse> queryPage(UserOnlineRequest request) {

        Collection<String> keys = redisUtils.keys(prefix + "*");
        List<UserOnlineEntity> userOnlineList = new ArrayList<>();
        for(String key : keys){
            String userJson = (String)redisUtils.get(key);
            UserOnlineEntity userOnlineEntity = JSONObject.parseObject(userJson,UserOnlineEntity.class);
            userOnlineEntity.setToken(key.substring(key.lastIndexOf(":")+1));
            userOnlineList.add(userOnlineEntity);
        }

        if(StringUtil.isNotEmpty(request.getUsername())){
            userOnlineList = userOnlineList.stream().filter( p -> StringUtils.containsIgnoreCase(p.getUsername(), request.getUsername())).collect(Collectors.toList());
        }

        if(!SecurityUtils.isSupperAdminAuth()){
            userOnlineList = userOnlineList.stream().filter( p -> StringUtils.containsIgnoreCase(p.getUsername(),SecurityUtils.getLoginUser().getUsername())).collect(Collectors.toList());
        }


        Collections.reverse(userOnlineList);
        long total = userOnlineList.size();
        userOnlineList = userOnlineList.stream().sorted(Comparator.comparing(UserOnlineEntity::getLoginTime).reversed())
                .skip((request.getPageNum()-1)*request.getPageSize()).limit(request.getPageSize()).collect(Collectors.toList());
        return PageResponse.of(total,userOnlineList,UserOnlineResponse.class);
    }

    @Override
    public void delete(String[] tokenIds) {

        if(tokenIds == null || tokenIds.length == 0){
            throw new SoException("踢出失败");
        }
        for(String token : tokenIds){
            redisUtils.del(prefix+token);
        }
    }
}
