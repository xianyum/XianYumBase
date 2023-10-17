package cn.xianyum.proxy.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.Result;
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
@RequestMapping(value = "/xianyum-proxy/v1/proxy")
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
    @Permission(publicApi = true)
	public Result getPage(ProxyRequest request) {
		PageResponse<ProxyResponse> response = proxyService.getPage(request);
        return Result.page(response);
	}

    /**
     * 客户端管理根据ID查询数据
     *
     */
    @ApiOperation(value = "客户端管理根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    public Result getById(@PathVariable String id) {

        ProxyResponse response = proxyService.getById(id);
        return Result.success(response);
    }

    /**
     * 客户端管理保存数据
	 *
     */
    @ApiOperation(value = "客户端管理保存数据")
    @PostMapping(value = "/save")
    public Result save(@RequestBody ProxyRequest request) {

		Integer count = proxyService.save(request);
		if(count>0){
			return Result.success();
		}
		return Result.error("保存失败");
    }

    /**
     * 客户端管理修改数据
	 *
     */
    @ApiOperation(value = "客户端管理修改数据")
    @PutMapping(value = "/update")
    public Result update(@RequestBody ProxyRequest request) {

		Integer count = proxyService.update(request);
		if(count>0){
			return Result.success();
		}
		return Result.error("修改失败");
    }

	/**
     * 客户端管理删除数据
	 *
     */
    @ApiOperation(value = "客户端管理删除数据")
    @DeleteMapping(value = "/delete")
    public Result delete(@RequestBody String[] ids) {
		proxyService.deleteById(ids);
	    return Result.success();
    }

    /**
     * 获取客户端所有数据
     *
     */
    @ApiOperation(value = "获取客户端所有数据")
    @GetMapping(value = "/getList")
    public Result getList() {
        List<ProxyResponse> response = proxyService.getList();
        return Result.success(response);
    }


    /**
     * 数据刷入系统
     *
     */
    @ApiOperation(value = "刷入系统")
    @PutMapping(value = "/flushProxy")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result flushProxy() {
        proxyService.flushProxy();
        return Result.success();
    }


    /**
     * 发送客户端配置信息
     *
     */
    @SysLog("发送客户端配置信息")
    @ApiOperation(value = "发送客户端配置信息")
    @GetMapping(value = "/sendEmail/{id}")
    @Permission(publicApi = true)
    public Result sendEmail(@PathVariable String id) {
        String result = proxyService.sendEmail(id);
        return Result.success(result);
    }


    /**
     * 获取在线online数量
     *
     */
    @ApiOperation(value = "获取在线online数量")
    @GetMapping(value = "/getOnlineProxyCount")
    public Result getOnlineProxyCount() {
        return Result.success(proxyService.getOnlineProxyCount());
    }

    /**
     * 下载配置
     * @param id
     * @return
     */
    @ApiOperation(value = "生成客户端配置信息")
    @PostMapping(value = "/downloadConfig")
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
    @Permission(strategy = PermissionStrategy.ALLOW_CLIENT,publicApi = true)
    @SysLog(value = "重启java应用刷入写入量和读取量")
    public Result flushWriteAndReadBytes() throws Exception {
        Map<String, String> jobMapParams = new HashMap<>(2);
        jobMapParams.put("resetZeroFlag","Y");
        ReturnT returnT = proxyDetailsFlushWriteAndReadBytes.execute(jobMapParams,null);
        proxyLogService.setIgnoreSaveFlag();
        return Result.success(returnT.getValue());
    }
}
