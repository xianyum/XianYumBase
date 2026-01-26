package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.extension.entity.request.GoldPriceRequest;
import cn.xianyum.extension.entity.response.GoldPriceResponse;
import cn.xianyum.extension.service.GoldPriceService;
import java.util.List;

/**
 * 每日黄金金价(GoldPrice)Controller
 *
 * @author zhangwei
 * @since 2025-02-15 22:40:06
 */
@RestController
@RequestMapping("xym-extension/v1/goldPrice")
@Tag(name = "每日黄金金价接口")
public class GoldPriceController{

    @Autowired
    private GoldPriceService goldPriceService;

    /**
     * 分页查询每日黄金金价
	 *
     * @param request 查询实体
     * @return 分页数据
     */
	@Operation(summary = "分页查询每日黄金金价")
    @GetMapping(value = "/getPage")
    public Results<GoldPriceResponse> getPage(GoldPriceRequest request) {
		PageResponse<GoldPriceResponse> responsePage = goldPriceService.getPage(request);
        return Results.page(responsePage);
    }

    /**
     * 获取最新金价
     *
     * @return 分页数据
     */
    @Operation(summary = "获取最新金价")
    @GetMapping(value = "/getLatestPrice")
    @Permission(publicApi = true)
    public Results<GoldPriceResponse> getLatestPrice() {
        GoldPriceResponse response = goldPriceService.getLatestPrice();
        return Results.success(response);
    }


    /**
     * 获取金价趋势图
     *
     * @return 分页数据
     */
    @Operation(summary = "获取金价趋势图")
    @GetMapping(value = "/getTrend")
    @Permission(publicApi = true)
    public Results<List<GoldPriceResponse>> getTrend() {
        List<GoldPriceResponse> responseList = goldPriceService.getTrend();
        return Results.success(responseList);
    }

    /**
     * 获取K线图
     *
     * @return 分页数据
     */
    @Operation(summary = "获取K线图")
    @GetMapping(value = "/getKLine")
    @Permission(publicApi = true)
    public Results<?> getKLine() {
        List<List<Object>> responseList = goldPriceService.getKLine();
        return Results.success(responseList);
    }
}
