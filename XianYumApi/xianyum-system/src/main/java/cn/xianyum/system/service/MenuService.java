package cn.xianyum.system.service;

import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.entity.response.MenuResponse;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/5/25 19:35
 * @email 80616059@qq.com
 */
public interface MenuService{

    /**
     * 获取用户菜单列表
     */
    List<MenuResponse> getUserMenuList();

    List<MenuEntity> getChildPerms(List<MenuEntity> menus, int i);

    void recursionFn(List<MenuEntity> menus, MenuEntity t);

    boolean hasChild(List<MenuEntity> menus, MenuEntity tChild);

    List<MenuEntity> getChildList(List<MenuEntity> menus, MenuEntity tChild);

    List<MenuResponse> buildMenus(List<MenuEntity> menuChildList);
}
