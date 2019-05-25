package com.base.service.impl;

import com.base.common.exception.SoException;
import com.base.dao.UserMapper;
import com.base.entity.enums.DeleteTagEnum;
import com.base.entity.enums.UserStatusEnum;
import com.base.entity.po.UserEntity;
import com.base.entity.request.RegisterInfoRequest;
import com.base.service.iservice.RegisterService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void insert(RegisterInfoRequest request) {
        UserEntity userEntity = userMapper.queryByUserName(request.getUsername());
        if (userEntity != null) {
            throw new SoException("账号已被使用");
        }
        UserEntity bean = new UserEntity();
        bean.setUsername(request.getUsername());
        bean.setMobile(request.getMobile());
        bean.setEmail(request.getEmail());
        bean.setStatus(UserStatusEnum.ALLOW.getStatus());
        bean.setCreateTime(new Date());
        bean.setDelTag(DeleteTagEnum.Delete.getDeleteTag());
        String salt = RandomStringUtils.randomAlphanumeric(20);
        bean.setPassword(new Sha256Hash(request.getPassword(), salt).toHex());
        bean.setSalt(salt);
        //进行保存用户信息
        userMapper.save(bean);
    }
}
