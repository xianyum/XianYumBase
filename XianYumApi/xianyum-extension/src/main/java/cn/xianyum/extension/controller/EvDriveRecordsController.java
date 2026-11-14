package cn.xianyum.extension.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.BusinessLock;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.OperTypeEnum;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.Results;
import cn.xianyum.extension.entity.request.EvAutoReportRequest;
import cn.xianyum.extension.entity.request.EvTripRequest;
import cn.xianyum.extension.entity.response.EvAutoReportResponse;
import cn.xianyum.extension.entity.response.EvDriveRecordsAppReportResponse;
import cn.xianyum.extension.entity.response.EvTripResponse;
import cn.xianyum.extension.service.EvTripService;
import cn.xianyum.extension.service.EvDriveRecordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import cn.xianyum.extension.entity.response.EvDriveRecordsResponse;
import java.util.List;
import java.util.Map;

/**
 * 新能源车行驶记录(EvDriveRecords)Controller
 *
 * @author zhangwei
 * @since 2025-03-06 20:43:40
 */
@Slf4j
@RestController
@RequestMapping("xym-extension/v1/evDriveRecords")
@Tag(name = "新能源车行驶记录接口")
@OperLog(module = "新能源车行驶记录")
public class EvDriveRecordsController {

    @Autowired
    private EvDriveRecordsService evDriveRecordsService;

    @Autowired
    private EvTripService evTripService;

    /**
     * 分页查询新能源车行驶记录
     *
     * @param request 查询实体
     * @return 分页数据
     */
    @Operation(summary = "分页查询新能源车行驶记录")
    @GetMapping(value = "/getPage")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "分页查询新能源车行驶记录")
    public Results<EvDriveRecordsResponse> getPage(EvDriveRecordsRequest request) {
        PageResponse<EvDriveRecordsResponse> responsePage = evDriveRecordsService.getPage(request);
        return Results.page(responsePage);
    }

    /**
     * 根据ID查询新能源车行驶记录
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "根据ID查询新能源车行驶记录")
    @GetMapping("getById/{id}")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询新能源车行驶记录")
    public Results<EvDriveRecordsResponse> selectOne(@PathVariable Long id) {
        return Results.success(evDriveRecordsService.getById(id));
    }

    /**
     * 新能源车行驶记录保存数据
     *
     * @param request
     * @return 新增结果
     */
    @Operation(summary = "新能源车行驶记录保存数据")
    @PostMapping(value = "/save")
    @SaCheckPermission("ev-drive-records:save")
    @OperLog(operType = OperTypeEnum.ADD, desc = "新增新能源车行驶记录")
    public Results<Integer> insert(@RequestBody EvDriveRecordsRequest request) {
        return Results.success(this.evDriveRecordsService.save(request));
    }

    /**
     * 新能源车行驶记录更新数据
     *
     * @param request
     * @return 修改结果
     */
    @Operation(summary = "新能源车行驶记录更新数据")
    @PutMapping(value = "/update")
    @SaCheckPermission("ev-drive-records:update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "修改新能源车行驶记录")
    public Results<Integer> update(@RequestBody EvDriveRecordsRequest request) {
        return Results.success(this.evDriveRecordsService.update(request));
    }

    /**
     * 新能源车行驶记录批量删除数据
     *
     * @param ids 主键集合
     * @return 删除结果
     */
    @Operation(summary = "新能源车行驶记录批量删除数据")
    @DeleteMapping(value = "/delete")
    @SaCheckPermission("ev-drive-records:delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除新能源车行驶记录")
    public Results<Integer> delete(@RequestBody Long[] ids) {
        return Results.success(this.evDriveRecordsService.deleteById(ids));
    }


    /**
     * 分页查询新能源车行驶折线图
     *
     * @param request 查询实体
     * @return 分页数据
     */
    @Operation(summary = "分页查询新能源车行驶折线图")
    @GetMapping(value = "/getReportLineData")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "查询新能源车行驶折线图数据")
    public Results<List<Map<String, Object>>> getReportLineData(EvDriveRecordsRequest request) {
        List<Map<String, Object>> result = this.evDriveRecordsService.getReportLineData(request);
        return Results.success(result);
    }

    @Operation(summary = "获取App数据(本月,上月,近一年)")
    @GetMapping(value = "/getAppSummaryData")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "获取App统计数据")
    public Results<EvDriveRecordsAppReportResponse> getAppSummaryData() {
        EvDriveRecordsAppReportResponse result = this.evDriveRecordsService.getAppSummaryData();
        return Results.success(result);
    }

    @GetMapping(value = "/aiAnalysis")
    @Operation(summary = "行驶记录AI预测分析")
    @SaCheckPermission("ev-drive-records:ai:analysis")
    @OperLog(operType = OperTypeEnum.OTHER, desc = "行驶记录AI预测分析")
    public Results<String> aiAnalysis() {
        String result = this.evDriveRecordsService.aiAnalysis();
        return Results.success(result);
    }

    @Operation(summary = "上报车机遥感数据")
    @PostMapping(value = "/autoReport")
    @SaIgnore
    public Results<String> autoReport(@RequestBody EvAutoReportRequest request) {
        this.evDriveRecordsService.saveAutoReportData(request);
        return Results.success();
    }

    @Operation(summary = "获取车辆当前位置信息")
    @GetMapping(value = "/getCurrentLocation")
    @SaCheckPermission("ev-drive-records:location:get")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "获取车辆当前位置信息")
    public Results<EvAutoReportResponse> getCurrentLocation() {
        EvAutoReportResponse response = this.evDriveRecordsService.getCurrentLocation();
        return Results.success(response);
    }

    /**
     * 分页查询行程
     *
     * @param request 查询实体
     * @return 分页数据
     */
    @Operation(summary = "分页查询行程")
    @GetMapping(value = "/trip/getPage")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "分页查询行程")
    public Results<EvTripResponse> getTripPage(EvTripRequest request) {
        PageResponse<EvTripResponse> responsePage = evTripService.getPage(request);
        return Results.page(responsePage);
    }

    /**
     * 根据ID查询行程详情
     *
     * @param id 主键
     * @return 行程详情
     */
    @Operation(summary = "根据ID查询行程详情")
    @GetMapping("trip/getById/{id}")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询行程详情")
    public Results<EvTripResponse> getTripById(@PathVariable Long id) {
        return Results.success(evTripService.getById(id));
    }

    /**
     * 手动执行汇总行程
     */
    @Operation(summary = "手动执行汇总行程")
    @PostMapping(value = "/trip/summary")
    @BusinessLock(leaseTime = 30*60)
    @OperLog(operType = OperTypeEnum.OTHER, desc = "手动执行汇总行程")
    public Results<ReturnT> summaryEvTrip() {
        return Results.success(evTripService.doSummaryEvTrip());
    }
}
