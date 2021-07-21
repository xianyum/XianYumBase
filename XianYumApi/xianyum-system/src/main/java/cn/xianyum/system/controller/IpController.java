package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.entity.IpInfoEntity;
import cn.xianyum.common.service.IpService;
import cn.xianyum.common.utils.DataResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public DataResult getIpInfo(@RequestParam(required = false) String ip) {
        IpInfoEntity result = ipService.getIpInfo(ip);
        return DataResult.success(result);
    }
}
