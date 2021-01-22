package com.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.entity.po.UserEntity;
import com.base.entity.request.UserRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserMapper extends BaseMapper<UserEntity> {

    List<UserEntity> queryAll(@Param("user") UserRequest user, Page<UserEntity> page);

    List<UserEntity> getList(UserRequest request);

    int updateUser(UserRequest user);
}
