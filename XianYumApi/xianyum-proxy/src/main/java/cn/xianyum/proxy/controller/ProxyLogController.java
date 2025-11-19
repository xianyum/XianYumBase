package cn.xianyum.proxy.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.proxy.entity.request.ProxyLogRequest;
import cn.xianyum.proxy.entity.response.ProxyLogResponse;
import cn.xianyum.proxy.service.ProxyLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 远程代理日志接口
 *
 */
@Tag(name = "远程代理日志接口")
@RestController
@RequestMapping(value = "xym-proxy/v1/proxyLog")
@Slf4j
public class ProxyLogController {

	@Autowired
	private ProxyLogService proxyLogService;


    /**
     * 远程代理日志分页查询数据
     *
     */
	@Operation(summary = "远程代理日志分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission("@ps.hasPerm('xianyu:proxy-log:page')")
	public Results getPage(ProxyLogRequest request) {
        PageResponse<ProxyLogResponse> response = proxyLogService.getPage(request);
        return Results.page(response);
	}

    /**
     * 远程代理日志根据ID查询数据
     *
     */
    @Operation(summary = "远程代理日志根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission("@ps.hasPerm('xianyu:proxy-log:page')")
    public Results getById(@PathVariable Long id) {
        ProxyLogResponse response = proxyLogService.getById(id);
        return Results.success(response);
    }



    /**
     * 通过客户端上报数据
	 *
     */
    @Operation(summary = "通过客户端上报数据")
    @PostMapping(value = "/reportClientInfo")
    @Permission(publicApi = true)
    public Results reportClientInfo(@RequestBody ProxyLogRequest request) {
        Integer count = proxyLogService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }


	/**
     * 远程代理日志删除数据
	 *
     */
    @Operation(summary = "远程代理日志删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('xianyu:proxy-log:delete')")
    public Results delete(@RequestBody Long[] ids) {
		proxyLogService.deleteById(ids);
	    return Results.success();
    }


    @Operation(summary = "查询最近十条数据")
    @GetMapping(value = "/getLastProxyLog")
    public Results getLastProxyLog(ProxyLogRequest request) {
        List<ProxyLogResponse> response = proxyLogService.getLastProxyLog(request);
        return Results.success(response);
    }

}
