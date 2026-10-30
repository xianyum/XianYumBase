package cn.xianyum.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.IgnoreDataScope;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.system.entity.request.RoleRequest;
import cn.xianyum.system.entity.response.RoleResponse;
import cn.xianyum.system.service.RoleService;
import java.util.List;

/**
 * 角色管理(Role)Controller
 *
 * @author zhangwei
 * @since 2023-10-31 19:57:15
 */
@RestController
@RequestMapping("xym-system/v1/role")
@Tag(name = "角色管理接口")
public class RoleController{

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色管理
	 *
     * @param request 查询实体
     * @return 分页数据
     */
	@Operation(summary = "分页查询角色管理")
    @GetMapping(value = "/getPage")
    @IgnoreDataScope
    @SaCheckPermission("system:role:page")
    public Results<RoleResponse> getPage(RoleRequest request) {
		PageResponse<RoleResponse> responsePage = roleService.getPage(request);
        return Results.page(responsePage);
    }

    @Operation(summary = "查询角色数据")
    @GetMapping(value = "/getList")
    public Results<List<RoleResponse>> getList(RoleRequest request) {
        List<RoleResponse> responsePage = roleService.getList(request);
        return Results.success(responsePage);
    }

    /**
     * 根据ID查询角色管理
     *
     * @param id 主键
     * @return 单条数据
     */
	@Operation(summary = "根据ID查询角色管理")
    @GetMapping("getById/{id}")
    @IgnoreDataScope
    @SaCheckPermission("system:role:query")
    public Results<RoleResponse> selectOne(@PathVariable Long id) {
        return Results.success(roleService.getById(id));
    }

    /**
     * 角色管理保存数据
     *
     * @param request
     * @return 新增结果
     */
	@Operation(summary = "角色管理保存数据")
    @PostMapping(value = "/save")
    @SaCheckPermission("system:role:save")
    public Results<?> insert(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.save(request));
    }

    /**
     * 角色管理更新数据
     *
     * @param request
     * @return 修改结果
     */
	@Operation(summary = "角色管理更新数据")
    @PutMapping(value = "/update")
    @SaCheckPermission("system:role:update")
    public Results<?> update(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.update(request));
    }

    @Operation(summary = "更新角色状态")
    @PutMapping(value = "/changeStatus")
    @SaCheckPermission("system:role:update")
    public Results<?> changeStatus(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.changeStatus(request));
    }


    @Operation(summary = "更新角色权限范围")
    @PutMapping(value = "/changeDataScope")
    @SaCheckPermission("system:role:authorize")
    public Results<?> changeDataScope(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.changeDataScope(request));
    }

    @Operation(summary = "菜单授权")
    @PutMapping(value = "/authorizationMenu")
    @SaCheckPermission("system:role:authorize")
    public Results<?> authorizationMenu(@RequestBody RoleRequest request) {
        return Results.success(this.roleService.authorizationMenu(request));
    }

    /**
     * 角色管理批量删除数据
     *
     * @param ids 主键集合
     * @return 删除结果
     */
	@Operation(summary = "角色管理批量删除数据")
    @DeleteMapping(value = "/delete")
    @SaCheckPermission("system:role:delete")
    public Results<?> delete(@RequestBody Long[] ids) {
        return Results.success(this.roleService.deleteById(ids));
    }
}
