package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.utils.Result;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.entity.request.SystemConstantRequest;
import cn.xianyum.system.entity.response.SystemConstantResponse;
import cn.xianyum.system.service.SystemConstantService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2020/11/3 19:25
 */
@RestController
@RequestMapping("/xianyum-system/v1/systemConstant")
@Api(tags = "系统常用参数")
public class SystemConstantController {

    @Autowired
    private SystemConstantService systemConstantService;

    @GetMapping("/getPublicConstant/{key}")
    @SysLog(value = "获取公开系统可见参数")
    @ApiOperation(value = "获取系统可见参数")
    @Permission(publicApi = true)
    public Result getPublicConstant(@PathVariable String key) {
        SystemConstantEntity response = systemConstantService.getPublicConstant(key);
        return Result.success(response);
    }


    @GetMapping("/getPrivateConstant/{key}")
    @ApiOperation(value = "获取私有系统内部参数")
    public Result getPrivateConstant(@PathVariable String key) {
        SystemConstantEntity response = systemConstantService.getPrivateConstant(key);
        return Result.success(response);
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新系统参数", httpMethod = "PUT")
    public Result update(@RequestBody SystemConstantRequest request) {
        int count = systemConstantService.update(request);
        return Result.success(count);
    }


    /**
     * 系统常用常量分页查询数据
     *
     */
    @ApiOperation(value = "系统常量分页查询数据")
    @GetMapping(value = "/getPage")
    public Result getPage(SystemConstantRequest request) {

        IPage<SystemConstantResponse> response = systemConstantService.getPage(request);
        return Result.page(response);
    }

    /**
     * 系统常用常量根据ID查询数据
     *
     */
    @ApiOperation(value = "系统常量根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    public Result getById(@PathVariable String id) {
        SystemConstantResponse response = systemConstantService.getById(id);
        return Result.success(response);
    }


    /**
     * 系统常用常量保存数据
     *
     */
    @ApiOperation(value = "系统常量保存数据")
    @PostMapping(value = "/save")
    public Result save(@RequestBody SystemConstantRequest request) {

        Integer count = systemConstantService.save(request);
        if(count>0){
            return Result.success();
        }
        return Result.error("保存失败");
    }

    /**
     * 系统常用常量删除数据
     *
     */
    @ApiOperation(value = "系统常量删除数据")
    @DeleteMapping(value = "/delete")
    public Result delete(@RequestParam String key) {
        systemConstantService.deleteByKey(key);
        return Result.success();
    }


    @ApiOperation(value = "系统常量清除缓存")
    @GetMapping(value = "/deleteRedisCache")
    public Result deleteRedisCache(@RequestParam String key) {
        systemConstantService.deleteRedisCache(key);
        return Result.success();
    }


    @ApiOperation(value = "从缓存中获取系统常量")
    @GetMapping(value = "/getRedisCache")
    public Result getRedisCache(@RequestParam String key) {
        SecurityUtils.allowAdminAuth();
        SystemConstantEntity byKeyFromRedis = systemConstantService.getByKeyFromRedis(key);
        return Result.success(byKeyFromRedis);
    }


    @ApiOperation(value = "刷新系统常量")
    @DeleteMapping(value = "/refreshCache")
    public Result refreshCache() {
        SecurityUtils.allowAdminAuth();
        systemConstantService.refreshCache();
        return Result.success();
    }
}
