package cn.xianyum.framwork.security.service;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.UserStatusEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * @author zhangwei
 * @date 2021/7/15 20:43
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userService.queryByUserName(username);

        if(user == null) {
            throw new SoException("用户不存在");
        }
        if(user.getStatus() == UserStatusEnum.BAN.getStatus()){
            throw new SoException("账号已被锁定,请联系管理员");
        }

        LoginUser loginUser = JSONObject.parseObject(JSONObject.toJSONString(user),LoginUser.class);
        return loginUser;
    }
}
