package cn.xianyum.system.controller;


import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.service.MenuService;
import cn.xianyum.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Set;

/**
 * @author zhangwei
 * @date 2019/5/25 20:06
 * @email 80616059@qq.com
 */
@RestController
@RequestMapping("/menu")
@Api(tags = "菜单接口")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    /**
     * 导航菜单
     */
    @GetMapping("/nav")
    @ApiOperation(value = "获取导航菜单以及权限", httpMethod = "GET")
    public DataResult nav(){
        List<MenuEntity> menuList = menuService.getUserMenuList();
        Set<String> permissions = userService.getPermissions();
        return DataResult.success().put("menuList", menuList).put("permissions",permissions);
    }
}
