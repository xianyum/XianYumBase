package cn.xianyum.system.controller;


import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.entity.response.MenuResponse;
import cn.xianyum.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author zhangwei
 * @date 2019/5/25 20:06
 * @email 80616059@qq.com
 */
@RestController
@RequestMapping("/system/menu")
@Api(tags = "菜单接口")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 导航菜单
     */
    @GetMapping("/nav")
    @ApiOperation(value = "获取导航菜单以及权限", httpMethod = "GET")
    public DataResult nav(){
        List<MenuResponse> menuResponses = menuService.getUserMenuList();
        return DataResult.success(menuResponses);
    }
}
