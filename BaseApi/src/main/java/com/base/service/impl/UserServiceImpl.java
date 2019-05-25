package com.base.service.impl;

import com.base.common.exception.SoException;
import com.base.common.utils.AuthUserToken;
import com.base.common.utils.StringUtil;
import com.base.dao.UserMapper;
import com.base.entity.enums.DeleteTagEnum;
import com.base.entity.po.UserEntity;
import com.base.entity.request.UpdatePasswordRequest;
import com.base.entity.request.UserInfoRequest;
import com.base.service.iservice.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserEntity queryByUserName(String username) {
        return userMapper.queryByUserName(username);
    }

    @Override
    public List<UserEntity> queryAll(String username) {
        UserEntity user = AuthUserToken.getUser();
        //如果是管理员查询所有
        return userMapper.queryAll(username,null);
    }

    @Override
    public void deleteById(Long[] userIds) {
        if(userIds ==null || userIds.length <= 0){
            throw new SoException("非法传参");
        }
        for (Long id : userIds) {
            userMapper.deleteById(id);
        }
    }

    @Override
    public UserEntity selectOneById(Long id) {
        if(id == null){
            throw new SoException("非法传参");
        }
        return userMapper.queryByUserId(id);
    }

    @Override
    public void save(UserEntity user) {
        UserEntity userInfo = userMapper.queryByUserName(user.getUsername());
        if(userInfo != null){
            throw new SoException("此用户名已被使用！");
        }
        user.setCreateTime(new Date());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
        user.setSalt(salt);
        user.setDelTag(DeleteTagEnum.Delete.getDeleteTag());
        user.setPermissionStatus(user.getStatus());
        userMapper.save(user);
    }

    @Override
    public void update(UserEntity user) {
        UserEntity bean = userMapper.queryByUserId(user.getId().longValue());
        if(bean != null && !bean.getUsername().equals(user.getUsername())){
            UserEntity userInfo = userMapper.queryByUserName(user.getUsername());
            if(userInfo != null){
                throw new SoException("此用户名已被使用！");
            }
        }else if(bean == null){
            throw new SoException("非法传参");
        }
        if(StringUtil.isEmpty(user.getPassword())){
            //不传密码的话，就不更新密码
            user.setPassword(null);
            user.setSalt(null);
        }else {
            String salt = RandomStringUtils.randomAlphanumeric(20);
            user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
            user.setSalt(salt);
        }
        userMapper.update(user);
    }

    @Override
    public boolean updatePassword(UpdatePasswordRequest info) {
        UserEntity userEntity = (UserEntity)SecurityUtils.getSubject().getPrincipal();
        String password = info.getPassword();
        if( !new Sha256Hash(password, userEntity.getSalt()).toHex().equals(userEntity.getPassword())){
            return false;
        }
        UserEntity user = new UserEntity();
        user.setId(userEntity.getId());
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        user.setPassword(new Sha256Hash(info.getNewPassword(), salt).toHex());
        userMapper.update(user);
        return true;
    }

    @Override
    public UserEntity queryByPhone(String phone) {
        return userMapper.queryByPhone(phone);
    }

    /**
     * 获取权限标识
     * @return
     */
    @Override
    public Set<String> getUserPermissions() {
        UserEntity user = AuthUserToken.getUser();
        Set<String> set = new HashSet<>();
        set.add(user.getPermissionName());
        return set;
    }

    @Override
    public void updateInfo(UserEntity user) {
        userMapper.updateInfo(user);
    }

    @Override
    public void saveInfo(UserInfoRequest request) {
        UserEntity userInfo = userMapper.queryByUserName(request.getUsername());
        if(userInfo != null){
            throw new SoException("此用户名已被使用！");
        }
        request.setCreateTime(new Date());
        String salt = RandomStringUtils.randomAlphanumeric(20);
        request.setSalt(salt);
        request.setPassword(new Sha256Hash(request.getPassword(), salt).toHex());
        request.setDelTag(DeleteTagEnum.Delete.getDeleteTag());
        userMapper.saveInfo(request);
    }

    @Override
    public List<UserEntity> getByStatus(UserEntity user) {
        return userMapper.queryByStatus(user.getPermissionStatus());
    }
}
