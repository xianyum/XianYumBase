package com.base.dao;

import com.base.entity.po.UserEntity;
import com.base.entity.request.UserInfoRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    UserEntity queryByUserName(@Param("username") String username);

    UserEntity queryByUserId(@Param("userId") Long userId);

    List<UserEntity> queryAll(@Param("username") String username,@Param("userId") Long userId);

    void deleteById(Long id);

    Integer save(UserEntity user);

    void update(UserEntity user);

    UserEntity queryByPhone(@Param("phone") String phone);

    List<UserEntity> getUserPermissions(@Param("userId") Long userId);

    void updateInfo(UserEntity user);

    void saveInfo(UserInfoRequest request);

    List<UserEntity> queryByStatus(@Param("permissionStatus") Integer permissionStatus);
}
