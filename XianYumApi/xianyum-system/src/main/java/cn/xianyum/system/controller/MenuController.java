package cn.xianyum.system.controller;


import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Result;
import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.entity.request.MenuRequest;
import cn.xianyum.system.entity.response.MenuResponse;
import cn.xianyum.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/5/25 20:06
 * @email 80616059@qq.com
 */
@RestController
@RequestMapping("/xianyum-system/v1/menu")
@Api(tags = "菜单接口")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 导航栏菜单
     */
    @GetMapping("/nav")
    @ApiOperation(value = "获取导航菜单以及权限", httpMethod = "GET")
    public Result nav(){
        List<MenuResponse> menuResponses = menuService.getUserMenuList();
        return Result.success(menuResponses);
    }

    /**
     * 查询菜单列表
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "查询菜单列表")
    public Result selectMenuList(MenuRequest menuRequest){
        List<MenuEntity> menuResponses = menuService.selectMenuList(menuRequest);
        return Result.success(menuResponses);
    }


    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    @ApiOperation(value = "根据菜单编号获取详细信息", httpMethod = "GET")
    public Result getInfo(@PathVariable Long menuId) {
        return Result.success(menuService.selectMenuById(menuId));
    }


    /**
     * 保存菜单信息
     */
    @PostMapping(value = "/save")
    @ApiOperation(value = "保存菜单信息", httpMethod = "POST")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result save(@RequestBody MenuEntity menuEntity) {
        int count = menuService.save(menuEntity);
        return Result.success();
    }

    /**
     * 更新菜单信息
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "保存菜单信息", httpMethod = "PUT")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result update(@RequestBody MenuEntity menuEntity) {
        int count = menuService.update(menuEntity);
        return Result.success();
    }


    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    @ApiOperation(value = "删除菜单", httpMethod = "DELETE")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result remove(@PathVariable("menuId") Long menuId) {
        return Result.success(menuService.deleteMenuById(menuId));
    }
}
