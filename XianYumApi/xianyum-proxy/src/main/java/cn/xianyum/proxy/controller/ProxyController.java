package cn.xianyum.proxy.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.Results;
import cn.xianyum.proxy.entity.request.ProxyRequest;
import cn.xianyum.proxy.entity.response.ProxyResponse;
import cn.xianyum.proxy.service.ProxyLogService;
import cn.xianyum.proxy.service.ProxyService;
import cn.xianyum.proxy.task.ProxyDetailsFlushWriteAndReadBytes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端管理接口
 *
 */
@Api(tags = "客户端管理接口")
@RestController
@RequestMapping(value = "xym-proxy/v1/proxy")
@Slf4j
public class ProxyController {

	@Autowired
	private ProxyService proxyService;

    @Autowired
    private ProxyLogService proxyLogService;

    @Autowired
    private ProxyDetailsFlushWriteAndReadBytes proxyDetailsFlushWriteAndReadBytes;

    /**
     * 客户端管理分页查询数据
     *
     */
	@ApiOperation(value = "客户端管理分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission(value = "@ps.hasPerm('xianyu:proxy:page')",ignoreDataScope = true)
	public Results getPage(ProxyRequest request) {
		PageResponse<ProxyResponse> response = proxyService.getPage(request);
        return Results.page(response);
	}

    /**
     * 客户端管理根据ID查询数据
     *
     */
    @ApiOperation(value = "客户端管理根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission(value = "@ps.hasPerm('xianyu:proxy:query')",ignoreDataScope = true)
    public Results getById(@PathVariable String id) {
        ProxyResponse response = proxyService.getById(id);
        return Results.success(response);
    }

    /**
     * 客户端管理保存数据
	 *
     */
    @ApiOperation(value = "客户端管理保存数据")
    @PostMapping(value = "/save")
    @Permission("@ps.hasPerm('xianyu:proxy:save')")
    public Results save(@RequestBody ProxyRequest request) {

		Integer count = proxyService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }

    /**
     * 客户端管理修改数据
	 *
     */
    @ApiOperation(value = "客户端管理修改数据")
    @PutMapping(value = "/update")
    @Permission("@ps.hasPerm('xianyu:proxy:update')")
    public Results update(@RequestBody ProxyRequest request) {

		Integer count = proxyService.update(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("修改失败");
    }

	/**
     * 客户端管理删除数据
	 *
     */
    @ApiOperation(value = "客户端管理删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('xianyu:proxy:delete')")
    public Results delete(@RequestBody String[] ids) {
		proxyService.deleteById(ids);
	    return Results.success();
    }

    /**
     * 获取客户端所有数据
     *
     */
    @ApiOperation(value = "获取客户端所有数据")
    @GetMapping(value = "/getList")
    public Results getList() {
        List<ProxyResponse> response = proxyService.getList();
        return Results.success(response);
    }


    /**
     * 数据刷入系统
     *
     */
    @ApiOperation(value = "刷入系统")
    @PutMapping(value = "/flushProxy")
    @Permission("@ps.hasPerm('xianyu:proxy:flush')")
    public Results flushProxy() {
        proxyService.flushProxy();
        return Results.success();
    }


    /**
     * 发送客户端配置信息
     *
     */
    @ApiOperation(value = "发送客户端配置信息")
    @GetMapping(value = "/sendEmail/{id}")
    @Permission(value = "@ps.hasPerm('xianyu:proxy:send-email')",ignoreDataScope = true)
    public Results sendEmail(@PathVariable String id) {
        String result = proxyService.sendEmail(id);
        return Results.success(result);
    }


    /**
     * 获取在线online数量
     *
     */
    @ApiOperation(value = "获取在线online数量")
    @GetMapping(value = "/getOnlineProxyCount")
    public Results getOnlineProxyCount() {
        return Results.success(proxyService.getOnlineProxyCount());
    }


    /**
     * 获取可绑定的用户列表
     *
     */
    @ApiOperation(value = "获取可绑定的用户列表")
    @GetMapping(value = "/getProxyBindUser")
    @Permission("@ps.hasPerm('xianyu:proxy:save')")
    public Results getProxyBindUser(@RequestParam(required = false) String id) {
        return Results.success(proxyService.getProxyBindUser(id));
    }


    /**
     * 获取当前用户绑定的客户端配置
     *
     */
    @ApiOperation(value = "获取当前用户绑定的客户端配置")
    @GetMapping(value = "/getCurrentProxy")
    public Results getCurrentProxy() {
        return Results.success(proxyService.getCurrentProxy());
    }

    /**
     * 下载配置
     * @param id
     * @return
     */
    @ApiOperation(value = "生成客户端配置信息")
    @PostMapping(value = "/downloadConfig")
    @Permission("@ps.hasPerm('xianyu:proxy:download')")
    public void downloadConfig(@RequestParam String id, HttpServletResponse response) {
        PrintWriter writer = null;
        try {
            String configInfo = proxyService.downloadConfig(id);
            response.setContentType("text/plain");
            response.setCharacterEncoding("utf-8");
            writer = response.getWriter();
            writer.println(configInfo);
        }catch (Exception e){
            log.info("下载远程配置config信息异常.",e);
        }finally {
            if(writer != null){
                writer.close();
            }
        }
    }


    @ApiOperation(value = "重启java应用刷入写入量和读取量")
    @GetMapping(value = "/flushWriteAndReadBytes")
    @Permission(publicApi = true)
    public Results flushWriteAndReadBytes() throws Exception {
        Map<String, String> jobMapParams = new HashMap<>(2);
        jobMapParams.put("resetZeroFlag","Y");
        ReturnT returnT = proxyDetailsFlushWriteAndReadBytes.execute(jobMapParams,null);
        proxyLogService.setIgnoreSaveFlag();
        return Results.success(returnT.getValue());
    }
}
