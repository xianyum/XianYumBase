package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.extension.entity.po.IpInfoEntity;
import cn.xianyum.extension.service.IpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帮助文档：https://gitee.com/lionsoul/ip2region
 * @author zhangwei
 * @date 2020/4/1 13:45
 */
@RestController
@RequestMapping("xym-extension/v1/ip")
@Api(tags = "ip地理位置接口")
public class IpController {

    @Autowired
    private IpService ipService;

    @GetMapping("/getIpInfo")
    @ApiOperation(value = "查询IP地理位置")
    @Permission(publicApi = true)
    public Results getIpInfo(@RequestParam(required = false) String ip) {
        IpInfoEntity result = ipService.getIpInfo(ip);
        return Results.success(result);
    }
}
