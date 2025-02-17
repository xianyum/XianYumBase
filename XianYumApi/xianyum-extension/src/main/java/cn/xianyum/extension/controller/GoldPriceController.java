package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.common.entity.base.PageResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
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
@Api(tags = "每日黄金金价接口")
public class GoldPriceController{

    @Autowired
    private GoldPriceService goldPriceService;

    /**
     * 分页查询每日黄金金价
	 *
     * @param request 查询实体
     * @return 分页数据
     */
	@ApiOperation(value = "分页查询每日黄金金价")
    @GetMapping(value = "/getPage")
    public Results getPage(GoldPriceRequest request) {
		PageResponse<GoldPriceResponse> responsePage = goldPriceService.getPage(request);
        return Results.page(responsePage);
    }

    /**
     * 获取最新金价
     *
     * @return 分页数据
     */
    @ApiOperation(value = "获取最新金价")
    @GetMapping(value = "/getLatestPrice")
    @Permission(publicApi = true)
    public Results getLatestPrice() {
        GoldPriceResponse response = goldPriceService.getLatestPrice();
        return Results.success(response);
    }


    /**
     * 获取金价趋势图
     *
     * @return 分页数据
     */
    @ApiOperation(value = "获取金价趋势图")
    @GetMapping(value = "/getTrend")
    @Permission(publicApi = true)
    public Results getTrend() {
        List<GoldPriceResponse> responseList = goldPriceService.getTrend();
        return Results.success(responseList);
    }
}
