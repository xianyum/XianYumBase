package cn.xianyum.system.service.impl;

import cn.xianyum.common.enums.MenuType;
import cn.xianyum.system.dao.MenuMapper;
import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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


    /**
     * 获取用户菜单列表
     *
     */
    @Override
    public List<MenuEntity> getUserMenuList() {
        //系统管理员，拥有最高权限
//        if(userId == 1){
//            return getAllMenuList(null);
//        }

        //用户菜单列表
        List<Long> menuIdList = menuMapper.queryMenuID();
        return getAllMenuList(menuIdList);
    }

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId   父菜单ID
     * @param menuIdList 用户菜单ID
     */
    @Override
    public List<MenuEntity> queryListParentId(Long parentId, List<Long> menuIdList) {
        List<MenuEntity> menuList = queryListParentId(parentId);
        if(menuIdList == null){
            return menuList;
        }

        List<MenuEntity> userMenuList = new ArrayList<>();
        for(MenuEntity menu : menuList){
            if(menuIdList.contains(menu.getMenuId())){
                userMenuList.add(menu);
            }
        }
        return userMenuList;
    }

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId 父菜单ID
     */
    @Override
    public List<MenuEntity> queryListParentId(Long parentId) {
        return menuMapper.queryListParentId(parentId);
    }

    /**
     * 递归
     */
    private List<MenuEntity> getMenuTreeList(List<MenuEntity> menuList, List<Long> menuIdList){
        List<MenuEntity> subMenuList = new ArrayList<>();

        for(MenuEntity entity : menuList){
            //目录
            if(entity.getType() == MenuType.CATALOG.getValue()){
                entity.setList(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
            }
            subMenuList.add(entity);
        }

        return subMenuList;
    }

    /**
     * 获取所有菜单列表
     */
    private List<MenuEntity> getAllMenuList(List<Long> menuIdList){
        //查询根菜单列表
        List<MenuEntity> menuList = queryListParentId(0L, menuIdList);
        //递归获取子菜单
        getMenuTreeList(menuList, menuIdList);

        return menuList;
    }

}
