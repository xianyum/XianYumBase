package cn.xianyum.system.controller;


import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.entity.request.MenuRequest;
import cn.xianyum.system.entity.response.MenuResponse;
import cn.xianyum.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2019/5/25 20:06
 * @email 80616059@qq.com
 */
@RestController
@RequestMapping("xianyum-system/v1/menu")
@Api(tags = "菜单接口")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 导航栏菜单
     */
    @GetMapping("/nav")
    @ApiOperation(value = "获取导航菜单以及权限", httpMethod = "GET")
    public Results nav(){
        List<MenuResponse> menuResponses = menuService.getUserMenuList();
        return Results.success(menuResponses);
    }

    /**
     * 分页查询菜单列表
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "分页查询菜单列表")
    @Permission(value = "@ps.hasPerm('system:menu:page')",ignoreDataScope = true)
    public Results selectMenuList(MenuRequest menuRequest){
        List<MenuEntity> menuResponses = menuService.selectMenuList(menuRequest);
        return Results.success(menuResponses);
    }


    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    @ApiOperation(value = "根据菜单编号获取详细信息")
    @Permission(value = "@ps.hasPerm('system:menu:query')",ignoreDataScope = true)
    public Results getInfo(@PathVariable Long menuId) {
        return Results.success(menuService.selectMenuById(menuId));
    }


    /**
     * 保存菜单信息
     */
    @PostMapping(value = "/save")
    @ApiOperation(value = "保存菜单信息")
    @Permission("@ps.hasPerm('system:menu:save')")
    public Results save(@RequestBody MenuEntity menuEntity) {
        int count = menuService.save(menuEntity);
        return Results.success(count);
    }

    /**
     * 更新菜单信息
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "保存菜单信息")
    @Permission("@ps.hasPerm('system:menu:update')")
    public Results update(@RequestBody MenuEntity menuEntity) {
        int count = menuService.update(menuEntity);
        return Results.success(count);
    }


    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    @ApiOperation(value = "删除菜单")
    @Permission("@ps.hasPerm('system:menu:delete')")
    public Results remove(@PathVariable("menuId") Long menuId) {
        return Results.success(menuService.deleteMenuById(menuId));
    }


    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeSelect")
    public Results treeSelect(MenuRequest request) {
        List<MenuEntity> menus = menuService.selectMenuList(request);
        return Results.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeSelect/role")
    public Results treeSelectByRoleId(@RequestParam Long roleId) {
        Map<String,Object> resultMap = menuService.treeSelectByRoleId(roleId);
        return Results.success(resultMap);
    }
}
