package com.base.controller;


import com.base.common.utils.DataResult;
import com.base.entity.po.MenuEntity;
import com.base.service.iservice.MenuService;
import com.base.service.iservice.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;
    /**
     * 导航菜单
     */
    @GetMapping("/nav")
    @ApiOperation(value = "获取导航菜单以及权限", httpMethod = "GET", notes = "获取导航菜单以及权限")
    public DataResult nav(){
        List<MenuEntity> menuList = menuService.getAll();
        Set<String> userPermissions = userService.getUserPermissions();
        return DataResult.success().put("menuList", menuList).put("permissions",userPermissions);
    }
}
