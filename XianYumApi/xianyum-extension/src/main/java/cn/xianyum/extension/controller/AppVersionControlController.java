package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.entity.base.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.extension.entity.request.AppVersionControlRequest;
import cn.xianyum.extension.entity.response.AppVersionControlResponse;
import cn.xianyum.extension.service.AppVersionControlService;

/**
 * APP版本管理(AppVersionControl)Controller
 *
 * @author zhangwei
 * @since 2026-01-23 17:28:10
 */
@RestController
@RequestMapping("/xym-extension/v1/appVersionControl")
@Tag(name = "APP版本管理接口")
public class AppVersionControlController {

    @Autowired
    private AppVersionControlService appVersionControlService;

    /**
     * 分页查询APP版本管理
     *
     * @param request 查询实体
     * @return 分页数据
     */
    @Operation(summary = "分页查询APP版本管理")
    @GetMapping(value = "/getPage")
    public Results getPage(AppVersionControlRequest request) {
        PageResponse<AppVersionControlResponse> responsePage = appVersionControlService.getPage(request);
        return Results.page(responsePage);
    }

    /**
     * 根据ID查询APP版本管理
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "根据ID查询APP版本管理")
    @GetMapping("getById/{id}")
    public Results selectOne(@PathVariable Long id) {
        return Results.success(appVersionControlService.getById(id));
    }

    /**
     * APP版本管理保存数据
     *
     * @param request
     * @return 新增结果
     */
    @Operation(summary = "APP版本管理保存数据")
    @PostMapping(value = "/save")
    @Permission("@ps.hasPerm('app:version_control:save')")
    public Results insert(@RequestBody AppVersionControlRequest request) {
        return Results.success(this.appVersionControlService.save(request));
    }

    /**
     * APP版本管理更新数据
     *
     * @param request
     * @return 修改结果
     */
    @Operation(summary = "APP版本管理更新数据")
    @PutMapping(value = "/update")
    @Permission("@ps.hasPerm('app:version_control:update')")
    public Results update(@RequestBody AppVersionControlRequest request) {
        return Results.success(this.appVersionControlService.update(request));
    }

    /**
     * APP版本管理批量删除数据
     *
     * @param ids 主键集合
     * @return 删除结果
     */
    @Operation(summary = "APP版本管理批量删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('app:version_control:delete')")
    public Results delete(@RequestBody Long[] ids) {
        return Results.success(this.appVersionControlService.deleteById(ids));
    }

    @Operation(summary = "查询最新APP版本管理")
    @PostMapping("/getLastAppVersion")
    public Results getLastAppVersion(@RequestBody AppVersionControlRequest request) {
        return Results.success(appVersionControlService.getLastAppVersion(request));
    }
}
