package com.base.service.iservice;

import com.base.entity.po.UserEntity;
import com.base.entity.request.UpdatePasswordRequest;
import com.base.entity.request.UserInfoRequest;

import java.util.List;
import java.util.Set;

public interface UserService {

    UserEntity queryByUserName(String username);

    List<UserEntity> queryAll(String username);

    void deleteById(Long[] userIds);

    UserEntity selectOneById(Long id);

    void save(UserEntity user);

    void update(UserEntity user);

    boolean updatePassword(UpdatePasswordRequest info);

    UserEntity queryByPhone(String phone);

    Set<String> getUserPermissions();

    void updateInfo(UserEntity user);

    void saveInfo(UserInfoRequest request);

    List<UserEntity> getByStatus(UserEntity user);
}
