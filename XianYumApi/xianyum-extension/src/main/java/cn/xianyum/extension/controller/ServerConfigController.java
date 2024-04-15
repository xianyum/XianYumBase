package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.extension.entity.po.server.Server;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.extension.entity.request.ServerConfigRequest;
import cn.xianyum.extension.entity.response.ServerConfigResponse;
import cn.xianyum.extension.service.ServerConfigService;

import java.util.List;

/**
 * 主机维护(ServerConfig)Controller
 *
 * @author makejava
 * @since 2024-04-02 22:27:34
 */
@RestController
@RequestMapping("xym-extension/v1/serverConfig")
@Api(tags = "主机维护接口")
public class ServerConfigController{

    @Autowired
    private ServerConfigService serverConfigService;

    /**
     * 分页查询主机维护
	 *
     * @param request 查询实体
     * @return 分页数据
     */
	@ApiOperation(value = "分页查询主机维护")
    @GetMapping(value = "/getPage")
    @Permission(value = "@ps.hasPerm('server-config:page')")
    public Results getPage(ServerConfigRequest request) {
		PageResponse<ServerConfigResponse> responsePage = serverConfigService.getPage(request);
        return Results.page(responsePage);
    }


    /**
     * 查询主机维护List
     *
     * @param request 查询实体
     */
    @ApiOperation(value = "查询主机维护list")
    @GetMapping(value = "/getList")
    @Permission(value = "@ps.hasPerm('server-config:query')")
    public Results getList(ServerConfigRequest request) {
        List<ServerConfigResponse> serverConfigResponseList = serverConfigService.getList(request);
        return Results.success(serverConfigResponseList);
    }

    /**
     * 根据ID查询主机维护
     *
     * @param id 主键
     * @return 单条数据
     */
	@ApiOperation(value = "根据ID查询主机维护")
    @GetMapping("getById/{id}")
    @Permission(value = "@ps.hasPerm('server-config:query')")
    public Results selectOne(@PathVariable Long id) {
        return Results.success(serverConfigService.getById(id));
    }

    /**
     * 主机维护保存数据
     *
     * @param request
     * @return 新增结果
     */
	@ApiOperation(value = "主机维护保存数据")
    @PostMapping(value = "/save")
    @Permission(value = "@ps.hasPerm('server-config:save')")
    public Results insert(@RequestBody ServerConfigRequest request) {
        return Results.success(this.serverConfigService.save(request));
    }

    /**
     * 主机维护更新数据
     *
     * @param request
     * @return 修改结果
     */
	@ApiOperation(value = "主机维护更新数据")
    @PutMapping(value = "/update")
    @Permission(value = "@ps.hasPerm('server-config:update')")
    public Results update(@RequestBody ServerConfigRequest request) {
        return Results.success(this.serverConfigService.update(request));
    }

    /**
     * 主机维护批量删除数据
     *
     * @param ids 主键集合
     * @return 删除结果
     */
	@ApiOperation(value = "主机维护批量删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(value = "@ps.hasPerm('server-config:delete')")
    public Results delete(@RequestBody Long[] ids) {
        return Results.success(this.serverConfigService.deleteById(ids));
    }


    @GetMapping("/current")
    @ApiOperation(value = "获取当前jar启动服务器相关信息")
    @Permission("@ps.hasPerm('monitor:server:list')")
    @SneakyThrows
    public Results getServerInfo(){
        Server server = new Server();
        server.copyTo();
        return Results.success(server);
    }
}
