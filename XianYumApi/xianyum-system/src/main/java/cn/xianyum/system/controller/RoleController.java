package cn.xianyum.system.controller;

import cn.xianyum.common.utils.Results;
import cn.xianyum.common.entity.base.PageResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.system.entity.request.RoleRequest;
import cn.xianyum.system.entity.response.RoleResponse;
import cn.xianyum.system.service.RoleService;

import java.util.List;

/**
 * 角色管理(Role)Controller
 *
 * @author makejava
 * @since 2023-10-31 19:57:15
 */
@RestController
@RequestMapping("xianyum-system/v1/role")
@Api(tags = "角色管理接口")
public class RoleController{

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色管理
	 *
     * @param request 查询实体
     * @return 分页数据
     */
	@ApiOperation(value = "分页查询角色管理")
    @GetMapping(value = "/getPage")
    public Results getPage(RoleRequest request) {
		PageResponse<RoleResponse> responsePage = roleService.getPage(request);
        return Results.page(responsePage);
    }

    @ApiOperation(value = "查询角色数据")
    @GetMapping(value = "/getList")
    public Results getList(RoleRequest request) {
        List<RoleResponse> responsePage = roleService.getList(request);
        return Results.success(responsePage);
    }

    /**
     * 根据ID查询角色管理
     *
     * @param id 主键
     * @return 单条数据
     */
	@ApiOperation(value = "根据ID查询角色管理")
    @GetMapping("getById/{id}")
    public Results selectOne(@PathVariable Long id) {
        return Results.success(roleService.getById(id));
    }

    /**
     * 角色管理保存数据
     *
     * @param request
     * @return 新增结果
     */
	@ApiOperation(value = "角色管理保存数据")
    @PostMapping(value = "/save")
    public Results insert(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.save(request));
    }

    /**
     * 角色管理更新数据
     *
     * @param request
     * @return 修改结果
     */
	@ApiOperation(value = "角色管理更新数据")
    @PutMapping(value = "/update")
    public Results update(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.update(request));
    }

    @ApiOperation(value = "更新角色状态")
    @PutMapping(value = "/changeStatus")
    public Results changeStatus(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.changeStatus(request));
    }


    @ApiOperation(value = "更新角色权限范围")
    @PutMapping(value = "/changeDataScope")
    public Results changeDataScope(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.changeDataScope(request));
    }

    @ApiOperation(value = "菜单授权")
    @PutMapping(value = "/authorizationMenu")
    public Results authorizationMenu(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.authorizationMenu(request));
    }

    /**
     * 角色管理批量删除数据
     *
     * @param ids 主键集合
     * @return 删除结果
     */
	@ApiOperation(value = "角色管理批量删除数据")
    @DeleteMapping(value = "/delete")
    public Results delete(@RequestBody Long[] ids) {
        return Results.success(this.roleService.deleteById(ids));
    }
}
