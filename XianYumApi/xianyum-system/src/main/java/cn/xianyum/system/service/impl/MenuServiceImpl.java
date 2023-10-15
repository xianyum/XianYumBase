package cn.xianyum.system.service.impl;

import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.dao.MenuMapper;
import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.entity.request.MenuRequest;
import cn.xianyum.system.entity.response.MenuMetaResponse;
import cn.xianyum.system.entity.response.MenuResponse;
import cn.xianyum.system.service.MenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zhangwei
 * @date 2019/5/25 19:36
 * @email 80616059@qq.com
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 获取用户菜单列表
     */
    @Override
    public List<MenuResponse> getUserMenuList() {
        String userId = SecurityUtils.getLoginUser().getId();

        //用户菜单列表
        List<MenuEntity> menus = menuMapper.selectMenuTreeByUserId(userId);
        List<MenuEntity> menuChildList = this.getChildPerms(menus, 0);

        return this.buildMenus(menuChildList);
    }

    @Override
    public List<MenuEntity> getChildPerms(List<MenuEntity> menus, int parentId) {
        List<MenuEntity> returnList = new ArrayList<MenuEntity>();
        for (Iterator<MenuEntity> iterator = menus.iterator(); iterator.hasNext(); ) {
            MenuEntity t = iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                this.recursionFn(menus, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    @Override
    public void recursionFn(List<MenuEntity> menus, MenuEntity t) {
        // 得到子节点列表
        List<MenuEntity> childList = this.getChildList(menus, t);
        t.setChildren(childList);
        for (MenuEntity tChild : childList) {
            if (this.hasChild(menus, tChild)) {
                recursionFn(menus, tChild);
            }
        }
    }

    @Override
    public boolean hasChild(List<MenuEntity> menus, MenuEntity tChild) {
        return this.getChildList(menus, tChild).size() > 0;
    }

    @Override
    public List<MenuEntity> getChildList(List<MenuEntity> list, MenuEntity tChild) {
        List<MenuEntity> tlist = new ArrayList<>();
        Iterator<MenuEntity> it = list.iterator();
        while (it.hasNext()) {
            MenuEntity n = it.next();
            if (n.getParentId().longValue() == tChild.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    @Override
    public List<MenuResponse> buildMenus(List<MenuEntity> menuChildList) {
        List<MenuResponse> routers = new LinkedList();
        for (MenuEntity menu : menuChildList) {
            MenuResponse router = new MenuResponse();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MenuMetaResponse(menu.getMenuName(), menu.getIcon(), Objects.equals("1", menu.getIsCache()), menu.getPath()));
            List<MenuEntity> cMenus = menu.getChildren();
            if (StringUtil.isNotEmpty(cMenus) && Constants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<MenuResponse> childrenList = new ArrayList<>();
                MenuResponse children = new MenuResponse();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MenuMetaResponse(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MenuMetaResponse(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<MenuResponse> childrenList = new ArrayList<>();
                MenuResponse children = new MenuResponse();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(Constants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MenuMetaResponse(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    @Override
    public List<MenuEntity> selectMenuList(MenuRequest menuRequest) {
        List<MenuEntity> menus = menuMapper.selectMenuList(menuRequest);
        return menus;
    }

    @Override
    public MenuEntity selectMenuById(Long menuId) {
        return menuMapper.selectById(menuId);
    }

    @Override
    public int save(MenuEntity menuEntity) {
        boolean isUnique = this.checkMenuNameUnique(menuEntity);
        if (!isUnique) {
            throw new SoException("新增菜单" + menuEntity.getMenuName() + "失败，菜单名称已存在");
        }

        if (Constants.YES_FRAME.equals(menuEntity.getIsFrame()) && !StringUtil.ishttp(menuEntity.getPath())) {
            throw new SoException("新增菜单'" + menuEntity.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        return menuMapper.insert(menuEntity);
    }

    @Override
    public int update(MenuEntity menuEntity) {
        if (!this.checkMenuNameUnique(menuEntity)) {
            throw new SoException("修改菜单'" + menuEntity.getMenuName() + "'失败，菜单名称已存在");
        } else if (Constants.YES_FRAME.equals(menuEntity.getIsFrame()) && !StringUtil.ishttp(menuEntity.getPath())) {
            throw new SoException("修改菜单'" + menuEntity.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menuEntity.getMenuId().equals(menuEntity.getParentId())) {
            throw new SoException("修改菜单'" + menuEntity.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        return menuMapper.updateById(menuEntity);
    }

    @Override
    public boolean checkMenuNameUnique(MenuEntity menuEntity) {
        Long menuId = Objects.isNull(menuEntity.getMenuId()) ? -1L : menuEntity.getMenuId();
        MenuEntity info = menuMapper.checkMenuNameUnique(menuEntity.getMenuName(), menuEntity.getParentId());
        if (Objects.nonNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public int deleteMenuById(Long menuId) {
        if (this.hasChildByMenuId(menuId)) {
           throw new SoException("存在子菜单,不允许删除");
        }
        return menuMapper.deleteById(menuId);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }


    public boolean isMenuFrame(MenuEntity menu) {
        return menu.getParentId().intValue() == 0 && Constants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(Constants.NO_FRAME);
    }


    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(MenuEntity menu) {
        return menu.getIsFrame().equals(Constants.NO_FRAME) && StringUtil.ishttp(menu.getPath());
    }


    /**
     * 内链域名特殊字符替换
     *
     * @return 替换后的内链域名
     */
    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[]{"", "", "", "/"});
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(MenuEntity menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(MenuEntity menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && Constants.TYPE_DIR.equals(menu.getMenuType())
                && Constants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(MenuEntity menu) {
        String component = Constants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = Constants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = Constants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(MenuEntity menu) {
        return menu.getParentId().intValue() != 0 && Constants.TYPE_DIR.equals(menu.getMenuType());
    }

}
