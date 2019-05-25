package com.base.service.iservice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.base.entity.po.UserEntity;
import com.base.entity.request.UpdatePasswordRequest;
import com.base.entity.request.UserRequest;

public interface UserService extends IService<UserEntity> {

    IPage<UserEntity> queryAll(UserRequest user);

    UserEntity queryByUserName(String username);

    void deleteById(Long[] userIds);

    UserEntity selectOneById(UserRequest user);

    int save(UserRequest user);

    int update(UserRequest user);

    boolean updatePassword(UpdatePasswordRequest info);
}
