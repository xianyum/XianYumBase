package cn.xianyum.proxy.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.utils.validator.ValidatorUtils;
import cn.xianyum.proxy.entity.request.ProxyDetailsRequest;
import cn.xianyum.proxy.entity.response.ProxyDetailsResponse;
import cn.xianyum.proxy.service.ProxyDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 客户端配置详细接口
 *
 */
@Tag(name = "客户端配置详细接口")
@RestController
@RequestMapping(value = "xym-proxy/v1/proxyDetails")
@Slf4j
public class ProxyDetailsController {

	@Autowired
	private ProxyDetailsService proxyDetailsService;

    /**
     * 客户端配置详细分页查询数据
     *
     */
	@Operation(summary = "客户端配置详细分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission(value = "@ps.hasPerm('xianyu:proxy-details:page')",ignoreDataScope = true)
	public Results<ProxyDetailsResponse> getPage(ProxyDetailsRequest request) {
		PageResponse<ProxyDetailsResponse> response = proxyDetailsService.getPage(request);
        return Results.page(response);
	}


    @Operation(summary = "获取当前用户客户端配置详情")
    @GetMapping(value = "/getCurrentProxyDetails")
    public Results<List<ProxyDetailsResponse>> getCurrentProxyDetails() {
        List<ProxyDetailsResponse> response = proxyDetailsService.getCurrentProxyDetails();
        return Results.success(response);
    }

    /**
     * 客户端配置详细根据ID查询数据
     *
     */
    @Operation(summary = "客户端配置详细根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission(value = "@ps.hasPerm('xianyu:proxy-details:query')",ignoreDataScope = true)
    public Results<ProxyDetailsResponse> getById(@PathVariable String id) {
        ProxyDetailsResponse response = proxyDetailsService.getById(id);
        return Results.success(response);
    }

    /**
     * 客户端配置详细保存数据
	 *
     */
    @Operation(summary = "客户端配置详细保存数据")
    @PostMapping(value = "/save")
    @Permission("@ps.hasPerm('xianyu:proxy-details:save')")
    public Results<?> save(@RequestBody ProxyDetailsRequest request) {
        ValidatorUtils.validateEntity(request);
		Integer count = proxyDetailsService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }

    /**
     * 客户端配置详细修改数据
	 *
     */
    @Operation(summary = "客户端配置详细修改数据")
    @PutMapping(value = "/update")
    @Permission("@ps.hasPerm('xianyu:proxy-details:update')")
    public Results<?> update(@RequestBody ProxyDetailsRequest request) {
        ValidatorUtils.validateEntity(request);
		Integer count = proxyDetailsService.update(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("修改失败");
    }

	/**
     * 客户端配置详细删除数据
	 *
     */
    @Operation(summary = "客户端配置详细删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('xianyu:proxy-details:delete')")
    public Results<?> delete(@RequestBody String[] ids) {
		proxyDetailsService.deleteById(ids);
	    return Results.success();
    }

}
