package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.service.SystemConstantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2020/11/3 19:25
 */
@RestController
@RequestMapping("/systemConstant")
@Api(tags = "系统常用参数")
public class SystemConstantController {

    @Autowired
    private SystemConstantService systemConstantService;

    @PostMapping("/getPublicConstant")
    @SysLog(value = "获取系统可见参数")
    @ApiOperation(value = "获取系统可见参数", httpMethod = "POST")
    public DataResult getPublicConstant(@RequestBody SystemConstantEntity request) {
        SystemConstantEntity response = systemConstantService.getPublicConstant(request);
        return DataResult.success(response);
    }


    @PostMapping("/getPrivateConstant")
    @ApiOperation(value = "获取系统内部参数", httpMethod = "POST")
    public DataResult getPrivateConstant(@RequestBody SystemConstantEntity request) {
        SystemConstantEntity response = systemConstantService.getPrivateConstant(request);
        return DataResult.success(response);
    }

    @PostMapping("/update")
    @Permissions(value = {"visitor","common"})
    @ApiOperation(value = "更新系统参数", httpMethod = "POST")
    public DataResult update(@RequestBody SystemConstantEntity request) {
        int count = systemConstantService.update(request);
        return DataResult.success(count);
    }
}
