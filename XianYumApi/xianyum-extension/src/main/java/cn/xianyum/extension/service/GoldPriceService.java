package cn.xianyum.extension.service;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.extension.entity.request.GoldPriceRequest;
import cn.xianyum.extension.entity.response.GoldPriceResponse;
import cn.xianyum.common.entity.base.PageResponse;

import java.util.List;
import java.util.Map;

/**
 * 每日黄金金价(GoldPrice)service层
 *
 * @author makejava
 * @since 2025-02-15 22:40:06
 */
public interface GoldPriceService{

	PageResponse<GoldPriceResponse> getPage(GoldPriceRequest request);

	/**
	 * 拉取每日金价
	 * @param jobParamsMap
	 * @param tool
	 * @return
	 */
	ReturnT pullGoldPrice(Map<String, String> jobParamsMap, SchedulerTool tool);

	/**
	 * 获取最新金价
	 * @return
	 */
	GoldPriceResponse getLatestPrice();

	/**
	 * 获取金价趋势图
	 * @return
	 */
    List<GoldPriceResponse> getTrend();
}
