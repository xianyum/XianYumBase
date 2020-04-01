package com.base.controller;

import com.base.common.annotation.SysLog;
import com.base.common.utils.DataResult;
import com.base.entity.po.IpInfoEntity;
import com.base.service.iservice.IpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 帮助文档：https://gitee.com/lionsoul/ip2region
 * @author zhangwei
 * @date 2020/4/1 13:45
 */
@RestController
@Api(tags = "ip地理位置接口")
public class IpController {

    @Autowired
    private IpService ipService;

    @GetMapping("/getIpInfo")
    @SysLog(value = "查询IP地理位置")
    @ApiOperation(value = "查询IP地理位置", httpMethod = "POST")
    public DataResult getIpInfo(@RequestParam String ip) {
        IpInfoEntity result = ipService.getIpInfo(ip);
        return DataResult.success(result);
    }
}
