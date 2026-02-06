package cn.xianyum.framwork.security.service;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.service.UserService;
import com.alibaba.fastjson2.JSONObject;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = SpringUtils.getBean(UserService.class).queryByUserName(username);
        if(user == null) {
            throw new SoException("用户/邮箱/手机号不存在");
        }
        if(user.getStatus() == YesOrNoEnum.NO.getStatus()){
            throw new SoException("账号已被锁定,请联系管理员");
        }

        LoginUser loginUser = JSONObject.parseObject(JSONObject.toJSONString(user),LoginUser.class);
        return loginUser;
    }
}
