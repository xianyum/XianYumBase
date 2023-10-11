package cn.xianyum.proxy.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.proxy.entity.request.ProxyRequest;
import cn.xianyum.proxy.service.ProxyService;
import cn.xianyum.proxy.task.ProxyDetailsFlushWriteAndReadBytes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2023/6/26 14:31
 */
@Api(tags = "客户端管理接口(公开)")
@RestController
@RequestMapping(value = "p1/proxy")
@Slf4j
public class PublicProxyController {

    @Autowired
    private ProxyDetailsFlushWriteAndReadBytes proxyDetailsFlushWriteAndReadBytes;

    @Autowired
    private ProxyService proxyService;


    @ApiOperation(value = "刷入系统")
    @GetMapping(value = "/flushProxy")
    @Permissions(strategy = PermissionStrategy.ALLOW_CLIENT)
    @SysLog(value = "远程调用刷入系统操作")
    public DataResult flushProxy() throws Exception {
        Map<String, String> jobMapParams = new HashMap<>();
        jobMapParams.put("resetZeroFlag","Y");
        ReturnT returnT = proxyDetailsFlushWriteAndReadBytes.execute(jobMapParams,null);
        return DataResult.success(returnT.getValue());
    }



    /**
     * 更新客户端信息
     *
     */
    @SysLog("更新客户端信息")
    @ApiOperation(value = "更新客户端信息")
    @PostMapping(value = "/updateClientInfo")
    public DataResult updateClientInfo(@RequestBody ProxyRequest request) {
        proxyService.updateClientInfo(request);
        return DataResult.success();
    }


    /**
     * 发送客户端配置信息
     *
     */
    @SysLog("客户端发送客户端配置信息")
    @ApiOperation(value = "客户端发送客户端配置信息")
    @GetMapping(value = "/sendEmail/{id}")
    public DataResult sendEmail(@PathVariable String id) {
        String result = proxyService.sendEmail(id);
        return DataResult.success(result);
    }
}
