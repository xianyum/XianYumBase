package com.base.service.iservice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.base.entity.po.MenuEntity;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/5/25 19:35
 * @email 80616059@qq.com
 */
public interface MenuService extends IService<MenuEntity> {

    /**
     * 获取用户菜单列表
     */
    List<MenuEntity> getUserMenuList();

    /**
     * 根据父菜单，查询子菜单
     * @param parentId 父菜单ID
     * @param menuIdList  用户菜单ID
     */
    List<MenuEntity> queryListParentId(Long parentId, List<Long> menuIdList);


    /**
     * 根据父菜单，查询子菜单
     * @param parentId 父菜单ID
     */
    List<MenuEntity> queryListParentId(Long parentId);
}
