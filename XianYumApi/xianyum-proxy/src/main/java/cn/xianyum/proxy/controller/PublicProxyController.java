package cn.xianyum.proxy.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.proxy.service.ProxyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private ProxyService proxyService;

    @ApiOperation(value = "刷入系统")
    @GetMapping(value = "/flushProxy")
    @Permissions(strategy = PermissionStrategy.ALLOW_CLIENT)
    public DataResult flushProxy() {
        proxyService.flushProxy();
        return DataResult.success();
    }
}
