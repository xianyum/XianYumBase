package cn.xianyum.system.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.request.LogRequest;
import cn.xianyum.system.entity.response.LogResponse;
import cn.xianyum.system.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * @author zhangwei
 * @date 2019/6/20 14:16
 */
@RestController
@RequestMapping("xym-system/v1/log")
@Tag(name = "系统日志接口")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 获取系统日志
     */
    @GetMapping("/getPage")
    @Operation(summary = "获取用户列表")
    @Permission(value = "@ps.hasPerm('monitor:operlog:page')",ignoreDataScope = true)
    public Results<LogResponse> getPage(LogRequest request){
        PageResponse<LogResponse> list = logService.getPage(request);
        return Results.page(list);
    }

    /**
     * 获取七天趋势图
     */
    @GetMapping("/getVisitCountCharts")
    @Operation(summary = "获取用户列表")
    public Results<List<LogResponse>> getVisitCountCharts(){
        List<LogResponse> responses = logService.getVisitCountCharts();
        return Results.success(responses);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除日志记录")
    @Permission("@ps.hasPerm('monitor:operlog:delete')")
    public Results<?> delete(@RequestBody String[] logIdS){
        try {
            logService.deleteById(logIdS);
            return Results.success();
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }

    @DeleteMapping("/truncateLog")
    @Operation(summary = "清空用户操作日志")
    @Permission("@ps.hasPerm('monitor:operlog:delete')")
    public Results<?> truncateLog(){
        logService.truncateLog();
        return Results.success();
    }


    /**
     * 获取接口日志数量
     *
     */
    @Operation(summary = "获取接口日志数量")
    @GetMapping(value = "/getLogCount")
    @Permission(publicApi = true)
    public Results<Long> getLogCount() {
        return Results.success(logService.getLogCount());
    }
}
