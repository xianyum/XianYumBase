package com.base.service.impl;

import com.base.common.utils.AuthUserToken;
import com.base.dao.MenuMapper;
import com.base.entity.po.MenuEntity;
import com.base.entity.po.UserEntity;
import com.base.service.iservice.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Override
    public List<MenuEntity> getAll() {
        UserEntity user = AuthUserToken.getUser();
        return menuMapper.getAll(user.getPermissionName());
    }
}
