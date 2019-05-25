package com.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.dao.MenuMapper;
import com.base.entity.po.MenuEntity;
import com.base.service.iservice.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/5/25 19:36
 * @email 80616059@qq.com
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<MenuEntity> getAll() {
        List<MenuEntity> list = menuMapper.selectList(
                new QueryWrapper<MenuEntity>().eq("del_tag", 0)
        );
        return list;
    }
}
