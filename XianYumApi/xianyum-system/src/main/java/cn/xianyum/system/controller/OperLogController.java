package cn.xianyum.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.request.OperLogRequest;
import cn.xianyum.system.entity.response.OperLogResponse;
import cn.xianyum.system.service.OperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志接口
 */
@RestController
@RequestMapping("xym-system/v1/log")
@Tag(name = "操作日志接口")
public class OperLogController {

    @Autowired
    private OperLogService operLogService;

    @GetMapping("/getPage")
    @Operation(summary = "分页查询操作日志")
    @SaCheckPermission("monitor:operlog:page")
    public Results<OperLogResponse> getPage(OperLogRequest request) {
        PageResponse<OperLogResponse> list = operLogService.getPage(request);
        return Results.page(list);
    }

    @GetMapping("/getVisitCountCharts")
    @Operation(summary = "获取访问量趋势图")
    public Results<List<OperLogResponse>> getVisitCountCharts() {
        List<OperLogResponse> responses = operLogService.getVisitCountCharts();
        return Results.success(responses);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除日志记录")
    @SaCheckPermission("monitor:operlog:delete")
    public Results<?> delete(@RequestBody Long[] ids) {
        operLogService.deleteByIds(ids);
        return Results.success();
    }

    @DeleteMapping("/truncateLog")
    @Operation(summary = "清空操作日志")
    @SaCheckPermission("monitor:operlog:delete")
    public Results<?> truncateLog() {
        operLogService.truncateAll();
        return Results.success();
    }

    @GetMapping(value = "/getLogCount")
    @Operation(summary = "获取日志总数")
    @SaIgnore
    public Results<Long> getLogCount() {
        return Results.success(operLogService.getOperLogCount());
    }
}
