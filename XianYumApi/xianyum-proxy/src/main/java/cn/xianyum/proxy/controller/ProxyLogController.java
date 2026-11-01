package cn.xianyum.proxy.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.OperTypeEnum;
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
@OperLog(module = "代理日志")
public class ProxyLogController {

	@Autowired
	private ProxyLogService proxyLogService;


    /**
     * 远程代理日志分页查询数据
     *
     */
	@Operation(summary = "远程代理日志分页查询数据")
	@GetMapping(value = "/getPage")
    @SaCheckPermission("xianyu:proxy-log:page")
	@OperLog(operType = OperTypeEnum.QUERY, desc = "分页查询代理日志")
	public Results<ProxyLogResponse> getPage(ProxyLogRequest request) {
        PageResponse<ProxyLogResponse> response = proxyLogService.getPage(request);
        return Results.page(response);
	}

    /**
     * 远程代理日志根据ID查询数据
     *
     *
     */
    @Operation(summary = "远程代理日志根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @SaCheckPermission("xianyu:proxy-log:page")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询代理日志")
    public Results<ProxyLogResponse> getById(@PathVariable Long id) {
        ProxyLogResponse response = proxyLogService.getById(id);
        return Results.success(response);
    }



    /**
     * 通过客户端上报数据
	 *
     */
    @Operation(summary = "通过客户端上报数据")
    @PostMapping(value = "/reportClientInfo")
    @SaIgnore
    @OperLog(operType = OperTypeEnum.OTHER, desc = "客户端上报数据")
    public Results<?> reportClientInfo(@RequestBody ProxyLogRequest request) {
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
    @SaCheckPermission("xianyu:proxy-log:delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除代理日志")
    public Results<?> delete(@RequestBody Long[] ids) {
		proxyLogService.deleteById(ids);
	    return Results.success();
    }


    @Operation(summary = "查询最近十条数据")
    @GetMapping(value = "/getLastProxyLog")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "查询最近10条代理日志")
    public Results<?> getLastProxyLog(ProxyLogRequest request) {
        List<ProxyLogResponse> response = proxyLogService.getLastProxyLog(request);
        return Results.success(response);
    }

}
