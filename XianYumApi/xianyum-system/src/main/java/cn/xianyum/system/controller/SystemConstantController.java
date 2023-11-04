package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.entity.request.SystemConstantRequest;
import cn.xianyum.system.entity.response.SystemConstantResponse;
import cn.xianyum.system.service.SystemConstantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2020/11/3 19:25
 */
@RestController
@RequestMapping("xianyum-system/v1/systemConstant")
@Api(tags = "系统常用参数")
public class SystemConstantController {

    @Autowired
    private SystemConstantService systemConstantService;

    @GetMapping("/getPublicConstant/{key}")
    @SysLog(value = "获取公开系统可见参数")
    @ApiOperation(value = "获取系统可见参数")
    @Permission(publicApi = true)
    public Results getPublicConstant(@PathVariable String key) {
        SystemConstantEntity response = systemConstantService.getPublicConstant(key);
        return Results.success(response);
    }


    @GetMapping("/getPrivateConstant/{key}")
    @ApiOperation(value = "获取私有系统内部参数")
    @Permission("@ps.hasPerm('system:config:private')")
    public Results getPrivateConstant(@PathVariable String key) {
        SystemConstantEntity response = systemConstantService.getPrivateConstant(key);
        return Results.success(response);
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新系统参数")
    @Permission("@ps.hasPerm('system:config:update')")
    public Results update(@RequestBody SystemConstantRequest request) {
        int count = systemConstantService.update(request);
        return Results.success(count);
    }


    /**
     * 系统常用常量分页查询数据
     *
     */
    @ApiOperation(value = "系统常量分页查询数据")
    @GetMapping(value = "/getPage")
    @Permission("@ps.hasPerm('system:config:page')")
    public Results getPage(SystemConstantRequest request) {
        PageResponse<SystemConstantResponse> response = systemConstantService.getPage(request);
        return Results.page(response);
    }

    /**
     * 系统常用常量根据ID查询数据
     *
     */
    @ApiOperation(value = "系统常量根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission("@ps.hasPerm('system:config:query')")
    public Results getById(@PathVariable String id) {
        SystemConstantResponse response = systemConstantService.getById(id);
        return Results.success(response);
    }


    /**
     * 系统常用常量保存数据
     *
     */
    @ApiOperation(value = "系统常量保存数据")
    @PostMapping(value = "/save")
    @Permission("@ps.hasPerm('system:config:save')")
    public Results save(@RequestBody SystemConstantRequest request) {

        Integer count = systemConstantService.save(request);
        if(count>0){
            return Results.success();
        }
        return Results.error("保存失败");
    }

    /**
     * 系统常用常量删除数据
     *
     */
    @ApiOperation(value = "系统常量删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('system:config:delete')")
    public Results delete(@RequestParam String key) {
        systemConstantService.deleteByKey(key);
        return Results.success();
    }


    @ApiOperation(value = "系统常量清除缓存")
    @GetMapping(value = "/deleteRedisCache")
    @Permission("@ps.hasPerm('system:config:cache')")
    public Results deleteRedisCache(@RequestParam String key) {
        systemConstantService.deleteRedisCache(key);
        return Results.success();
    }


    @ApiOperation(value = "从缓存中获取系统常量")
    @GetMapping(value = "/getRedisCache")
    @Permission("@ps.hasPerm('system:config:cache')")
    public Results getRedisCache(@RequestParam String key) {
        SystemConstantEntity byKeyFromRedis = systemConstantService.getByKeyFromRedis(key);
        return Results.success(byKeyFromRedis);
    }


    @ApiOperation(value = "刷新系统常量")
    @DeleteMapping(value = "/refreshCache")
    public Results refreshCache() {
        systemConstantService.refreshCache();
        return Results.success();
    }
}
