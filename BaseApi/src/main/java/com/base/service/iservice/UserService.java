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

    /**
     * 通过阿里authCode获取系统用户信息
     * @param authCode
     * @return
     */
    UserEntity getUserByAli(String authCode);
    /**
     * 通过QQ authCode获取系统用户信息
     * @param authCode
     * @return
     */
    UserEntity getUserByQq(String authCode);
}
