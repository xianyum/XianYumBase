package cn.xianyum.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.enums.PlatformTypeEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.dao.MenuMapper;
import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.entity.request.MenuRequest;
import cn.xianyum.system.entity.response.MenuMetaResponse;
import cn.xianyum.system.entity.response.MenuResponse;
import cn.xianyum.system.entity.response.MenuTreeSelect;
import cn.xianyum.system.service.MenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2019/5/25 19:36
 * @email 80616059@qq.com
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private StringRedisTemplate redisTemplate;

    @Value("${redis.menu.click_key}")
    private String menuClickKey;

    @Value("${redis.menu.click_rank}")
    private String menuClickRank;

    /**
     * 获取用户菜单列表
     */
    @Override
    public List<MenuResponse> getUserMenuList(String platformType) {
        String userId = SecurityUtils.getLoginUser().getId();
        if(SecurityUtils.isSupperAdminAuth()){
            userId = null;
        }
        //用户菜单列表
        List<MenuEntity> menus = menuMapper.selectMenuTreeByUserId(userId,platformType);
        List<MenuEntity> menuChildList = this.getChildPerms(menus, 0);
        return this.buildMenus(menuChildList);
    }

    @Override
    public List<MenuEntity> getChildPerms(List<MenuEntity> menus, int parentId) {
        List<MenuEntity> returnList = new ArrayList<>();
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
            router.setMenuId(menu.getMenuId());
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setIconBgColor(menu.getIconBgColor());
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
        MenuEntity menuEntity = menuMapper.selectById(menuId);
        return menuEntity;
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

        // 如果不是顶级，那么就需要判断平台类型是否一致
        if(menuEntity.getParentId() != 0L){
            MenuEntity parentMenuEntity = menuMapper.selectById(menuEntity.getParentId());
            if(Objects.nonNull(parentMenuEntity) && !parentMenuEntity.getPlatformType().equals(menuEntity.getPlatformType())){
                throw new SoException("平台类型必须和上级菜单类型保持一致");
            }
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

        // 如果不是顶级，那么就需要判断平台类型是否一致
        if(menuEntity.getParentId() != 0L){
            MenuEntity parentMenuEntity = menuMapper.selectById(menuEntity.getParentId());
            if(Objects.nonNull(parentMenuEntity) && !parentMenuEntity.getPlatformType().equals(menuEntity.getPlatformType())){
                throw new SoException("平台类型必须和上级菜单类型保持一致");
            }
        }
        return menuMapper.updateById(menuEntity);
    }

    @Override
    public boolean checkMenuNameUnique(MenuEntity menuEntity) {
        Long menuId = Objects.isNull(menuEntity.getMenuId()) ? -1L : menuEntity.getMenuId();
        MenuEntity info = menuMapper.checkMenuNameUnique(menuEntity.getMenuName(),menuEntity.getPlatformType(),menuEntity.getParentId());
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

    @Override
    public List<MenuTreeSelect> buildMenuTreeSelect(List<MenuEntity> menus) {
        List<MenuEntity> menuTrees = this.buildMenuTree(menus);
        return menuTrees.stream().map(MenuTreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public List<MenuEntity> buildMenuTree(List<MenuEntity> menus) {
        List<MenuEntity> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(MenuEntity::getMenuId).collect(Collectors.toList());
        for (Iterator<MenuEntity> iterator = menus.iterator(); iterator.hasNext();) {
            MenuEntity menu = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    @Override
    public Map<String, Object> treeSelectByRoleId(Long roleId) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("checkedKeys",this.selectMenuListByRoleId(roleId));
        resultMap.put("menus",this.buildMenuTreeSelect(this.selectMenuList(null)));
        return resultMap;
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    @Override
    public Set<String> getMenuPermission(String userId) {
        Set<String> resultPermissions = new HashSet<>();
        if(SecurityUtils.isSupperAdminAuth()){
            resultPermissions.add("*:*:*");
        }else{
            resultPermissions = this.menuMapper.selectMenuPermsByUserId(userId);
        }
        return resultPermissions;
    }

    /**
     * 菜单埋点上报
     *
     * @param menuRequest
     */
    @Override
    public void reportMenuClick(MenuEntity menuRequest) {
        if(Objects.isNull(menuRequest.getMenuId())){
            return;
        }
        try {
            // 1. 递增菜单点击次数（每个菜单独立计数）
            String menuKey = String.format(menuClickKey,SecurityUtils.getLoginUser().getId(),menuRequest.getMenuId());
            Long clickCount = redisTemplate.opsForValue().increment(menuKey, 1);
            // 2. 更新排行榜（zset有序集合，score为点击次数）
            String menuRankKey = String.format(menuClickRank,SecurityUtils.getLoginUser().getId());
            redisTemplate.opsForZSet().add(menuRankKey, String.valueOf(menuRequest.getMenuId()),clickCount);
        } catch (Exception e) {
            log.error("菜单埋点上报失败", e);
            throw e;
        }
    }

    /**
     * 获取菜单点击排名前N名
     *
     * @return
     */
    @Override
    public List<MenuResponse> getMenuClickRank() {
        // 取排名前4个的
        Integer topN = 4;
        List<MenuEntity> menus = menuMapper.selectMenuTreeByUserId(SecurityUtils.isSupperAdminAuth()?null:SecurityUtils.getLoginUser().getId(), PlatformTypeEnum.APP.getCode());
        List<MenuResponse> allMenuResponse = menus.stream()
                // 1. 过滤条件：平台类型为APP + 类型为菜单级别的
                .filter(item -> PlatformTypeEnum.APP.getCode().equals(item.getPlatformType())
                        && "C".equals(item.getMenuType()))
                // 2. 转换为MenuResponse
                .map(item -> BeanUtil.copyProperties(item, MenuResponse.class))
                .toList();
        if(CollUtil.isEmpty(allMenuResponse)){
            return List.of();
        }
        String menuRankKey = String.format(menuClickRank,SecurityUtils.getLoginUser().getId());
        Set<ZSetOperations.TypedTuple<String>> menuRankSet = redisTemplate.opsForZSet()
                .reverseRangeWithScores(menuRankKey, 0, -1);
        if(menuRankSet.isEmpty()){
            // 无排名数据，直接取前topN个
            return allMenuResponse.stream().limit(topN).collect(Collectors.toList());
        }
        // 定义最终返回的列表（指定初始容量为topN，减少扩容开销）
        List<MenuResponse> finalMenuList = new ArrayList<>(topN);

        // 先从排名中取菜单，直到凑够topN个或排名取完
        for (ZSetOperations.TypedTuple<String> tuple : menuRankSet) {
            if (finalMenuList.size() >= topN) {
                break; // 已凑够topN个，终止循环
            }
            Long menuId = Optional.ofNullable(tuple.getValue())
                    .map(Long::valueOf)
                    .orElse(0L);
            if (menuId == 0L) {
                continue; // 无效ID跳过
            }
            // 查找并添加对应菜单
            allMenuResponse.stream()
                    .filter(menu -> menuId.equals(menu.getMenuId()))
                    .findFirst()
                    .ifPresent(finalMenuList::add);
        }

        // 若排名数据不足topN个，从allMenuResponse中补充（排除已加入的）
        if (finalMenuList.size() < topN) {
            long needSupplement = topN - finalMenuList.size();
            allMenuResponse.stream().filter(menu -> finalMenuList.stream().noneMatch(m -> m.getMenuId().equals(menu.getMenuId()))).limit(needSupplement).forEach(finalMenuList::add);
        }

        // 返回最终凑够的topN个菜单
        return finalMenuList.stream().limit(topN).toList();
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
