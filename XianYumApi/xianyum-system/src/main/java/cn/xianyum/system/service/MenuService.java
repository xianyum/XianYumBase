package cn.xianyum.system.service;

import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.entity.request.MenuRequest;
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

    List<MenuEntity> selectMenuList(MenuRequest menuRequest);

    MenuEntity selectMenuById(Long menuId);

    int save(MenuEntity menuEntity);

    int update(MenuEntity menuEntity);

    boolean checkMenuNameUnique(MenuEntity menuEntity);

    int deleteMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByMenuId(Long menuId);
}
