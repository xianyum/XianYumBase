package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.extension.entity.request.ServerPortConfigRequest;
import cn.xianyum.extension.entity.response.ServerPortConfigResponse;
import cn.xianyum.extension.service.ServerPortConfigService;

/**
 * 主机端口维护(ServerPortConfig)Controller
 *
 * @author zhangwei
 * @since 2024-04-02 22:27:45
 */
@RestController
@RequestMapping("xym-extension/v1/serverPortConfig")
@Tag(name = "主机端口维护接口")
public class ServerPortConfigController{

    @Autowired
    private ServerPortConfigService serverPortConfigService;

    /**
     * 分页查询主机端口维护
	 *
     * @param request 查询实体
     * @return 分页数据
     */
	@Operation(summary = "分页查询主机端口维护")
    @GetMapping(value = "/getPage")
    @Permission(value = "@ps.hasPerm('zz:page')")
    public Results<ServerPortConfigResponse> getPage(ServerPortConfigRequest request) {
		PageResponse<ServerPortConfigResponse> responsePage = serverPortConfigService.getPage(request);
        return Results.page(responsePage);
    }

    /**
     * 根据ID查询主机端口维护
     *
     * @param id 主键
     * @return 单条数据
     */
	@Operation(summary = "根据ID查询主机端口维护")
    @GetMapping("getById/{id}")
    @Permission(value = "@ps.hasPerm('server-port-config:query')")
    public Results<ServerPortConfigResponse> selectOne(@PathVariable Long id) {
        return Results.success(serverPortConfigService.getById(id));
    }

    /**
     * 主机端口维护保存数据
     *
     * @param request
     * @return 新增结果
     */
	@Operation(summary = "主机端口维护保存数据")
    @PostMapping(value = "/save")
    @Permission(value = "@ps.hasPerm('server-port-config:save')")
    public Results<?> insert(@RequestBody ServerPortConfigRequest request) {
        return Results.success(this.serverPortConfigService.save(request));
    }

    /**
     * 主机端口维护更新数据
     *
     * @param request
     * @return 修改结果
     */
	@Operation(summary = "主机端口维护更新数据")
    @PutMapping(value = "/update")
    @Permission(value = "@ps.hasPerm('server-port-config:update')")
    public Results<?> update(@RequestBody ServerPortConfigRequest request) {
        return Results.success(this.serverPortConfigService.update(request));
    }

    /**
     * 主机端口维护批量删除数据
     *
     * @param ids 主键集合
     * @return 删除结果
     */
	@Operation(summary = "主机端口维护批量删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(value = "@ps.hasPerm('server-port-config:delete')")
    public Results<?> delete(@RequestBody Long[] ids) {
        return Results.success(this.serverPortConfigService.deleteById(ids));
    }
}
