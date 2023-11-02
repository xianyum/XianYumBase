package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.request.LogRequest;
import cn.xianyum.system.entity.response.LogResponse;
import cn.xianyum.system.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * @author zhangwei
 * @date 2019/6/20 14:16
 */
@RestController
@RequestMapping("xianyum-system/v1/log")
@Api(tags = "系统日志接口")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 获取系统日志
     */
    @GetMapping("/getPage")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public Results getPage(LogRequest request){
        PageResponse<LogResponse> list = logService.getPage(request);
        return Results.page(list);
    }

    /**
     * 获取七天趋势图
     */
    @GetMapping("/getVisitCountCharts")
    @ApiOperation(value = "获取用户列表", httpMethod = "POST")
    public Results getVisitCountCharts(){
        List<LogResponse> responses = logService.getVisitCountCharts();
        return Results.success(responses);
    }

    @DeleteMapping("/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    @ApiOperation(value = "删除日志记录", httpMethod = "POST")
    public Results delete(@RequestBody String[] logIdS){
        try {
            logService.deleteById(logIdS);
            return Results.success();
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }

    @DeleteMapping("/truncateLog")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    @ApiOperation(value = "清空日志", httpMethod = "POST")
    @SysLog(value = "清空用户操作日志")
    public Results truncateLog(){
        try {
            logService.truncateLog();
            return Results.success();
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }


    /**
     * 获取接口日志数量
     *
     */
    @ApiOperation(value = "获取接口日志数量")
    @GetMapping(value = "/getLogCount")
    public Results getLogCount() {
        return Results.success(logService.getLogCount());
    }
}
