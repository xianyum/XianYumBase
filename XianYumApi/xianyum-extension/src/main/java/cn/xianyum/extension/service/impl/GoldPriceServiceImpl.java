package cn.xianyum.extension.service.impl;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.enums.YesOrNoEnum;
import cn.xianyum.common.utils.*;
import cn.xianyum.extension.entity.response.GoldPriceApiResponse;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.common.entity.base.PageResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.xianyum.extension.entity.po.GoldPriceEntity;
import cn.xianyum.extension.entity.request.GoldPriceRequest;
import cn.xianyum.extension.entity.response.GoldPriceResponse;
import cn.xianyum.extension.service.GoldPriceService;
import cn.xianyum.extension.dao.GoldPriceMapper;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 每日黄金金价(GoldPrice)service层实现
 *
 * @author zhangwei
 * @since 2025-02-15 22:40:06
 */
@Service
@Slf4j
public class GoldPriceServiceImpl implements GoldPriceService {

	@Autowired
	private GoldPriceMapper goldPriceMapper;

	@Override
	public PageResponse<GoldPriceResponse> getPage(GoldPriceRequest request) {
		LambdaQueryWrapper<GoldPriceEntity> queryWrapper = Wrappers.<GoldPriceEntity>lambdaQuery()
				.gt(Objects.nonNull(request.getParams().get("beginTime")),GoldPriceEntity::getTime,request.getParams().get("beginTime"))
				.lt(Objects.nonNull(request.getParams().get("endTime")),GoldPriceEntity::getTime,request.getParams().get("endTime"))
				.orderByDesc(GoldPriceEntity::getCreateTime);
		Page<GoldPriceEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		IPage<GoldPriceEntity> pageResult = goldPriceMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,GoldPriceResponse.class);

	}


	/**
	 * 拉取每日金价
	 *
	 * @param jobParamsMap
	 * @param tool
	 * @return
	 */
	@Override
	public ReturnT pullGoldPrice(Map<String, String> jobParamsMap, SchedulerTool tool) {
		// 获取限制时间
		String limitHour = jobParamsMap.get("limitHour");
		DateTime dateTime = new DateTime();
		Integer hourOfDay = dateTime.getHourOfDay();
		int minuteOfHour = dateTime.getMinuteOfHour();
		if(StringUtil.isNotBlank(limitHour)){
			List<Integer> limitHourList = JSONObject.parseObject(limitHour, new TypeReference<List<Integer>>(){});
			if(limitHourList.contains(hourOfDay)){
				return ReturnT.SUCCESS;
			}
		}
		JSONObject jsonObject = SystemConstantUtils.getValueObjectByKey(SystemConstantKeyEnum.GOLD_CONFIG);
		String url = jsonObject.getString("url");
		String result = HttpUtils.getHttpInstance().sync(url).get().getBody().toString();
		GoldPriceApiResponse goldPriceApiResponse = JSONObject.parseObject(result, GoldPriceApiResponse.class);
		if(!goldPriceApiResponse.getResultCode().equals("200")){
			log.error("拉取每日黄金api异常,{}",result);
			return ReturnT.FAILURE;
		}
		// 获取足金99的价格
		GoldPriceApiResponse.Au99g au99g = goldPriceApiResponse.getResult().get(0).getAu99g();

		GoldPriceEntity goldPriceEntity = new GoldPriceEntity();
		goldPriceEntity.setVariety(au99g.getVariety());
		goldPriceEntity.setLatestPrice(BigDecimalUtils.formatString(au99g.getLatestPrice()));
		goldPriceEntity.setMaxPrice(BigDecimalUtils.formatString(au99g.getMaxPrice()));
		goldPriceEntity.setMinPrice(BigDecimalUtils.formatString(au99g.getMinPrice()));
		goldPriceEntity.setOpenPrice(BigDecimalUtils.formatString(au99g.getOpenPrice()));
		goldPriceEntity.setYesPrice(BigDecimalUtils.formatString(au99g.getYesPrice()));
		goldPriceEntity.setChangePercentage(au99g.getChangePercentage());
		goldPriceEntity.setTotalVol(BigDecimalUtils.formatString(au99g.getTotalVol()));
		try {
			goldPriceEntity.setTime(DateUtils.stringToDate(au99g.getTime(),DateUtils.DATE_TIME_PATTERN));
		}catch (Exception e){
			goldPriceEntity.setTime(new Date());
		}
		if(hourOfDay == 23 && minuteOfHour > 50){
			goldPriceEntity.setLatestTimeOfDay(YesOrNoEnum.YES.getStatus());
		}else{
			goldPriceEntity.setLatestTimeOfDay(YesOrNoEnum.NO.getStatus());
		}
		if(DateUtils.isWeek()){
			goldPriceEntity.setWeek(YesOrNoEnum.YES.getStatus());
		}else{
			goldPriceEntity.setWeek(YesOrNoEnum.NO.getStatus());
		}
		if(Objects.isNull(goldPriceEntity.getLatestPrice()) || Objects.isNull(goldPriceEntity.getOpenPrice())){
			return ReturnT.SUCCESS;
		}
		goldPriceMapper.insert(goldPriceEntity);
		return ReturnT.SUCCESS;
	}

	/**
	 * 获取最新金价
	 *
	 * @return
	 */
	@Override
	public GoldPriceResponse getLatestPrice() {
		LambdaQueryWrapper<GoldPriceEntity> queryWrapper = Wrappers.<GoldPriceEntity>lambdaQuery()
				.orderByDesc(GoldPriceEntity::getCreateTime)
				.last("limit 1");
		GoldPriceEntity goldPriceEntity = this.goldPriceMapper.selectOne(queryWrapper);
		return BeanUtils.copy(goldPriceEntity,GoldPriceResponse.class);
	}

	/**
	 * 获取金价趋势图
	 *
	 * @return
	 */
	@Override
	public List<GoldPriceResponse> getTrend() {
		LambdaQueryWrapper<GoldPriceEntity> queryWrapper = Wrappers.<GoldPriceEntity>lambdaQuery()
				.orderByAsc(GoldPriceEntity::getTime);
		return BeanUtils.copyList(this.goldPriceMapper.selectList(queryWrapper),GoldPriceResponse.class);
	}

	@Override
	public List<List<Object>> getKLine() {
		LambdaQueryWrapper<GoldPriceEntity> queryWrapper = Wrappers.<GoldPriceEntity>lambdaQuery()
				.eq(GoldPriceEntity::getLatestTimeOfDay,YesOrNoEnum.YES.getStatus())
				.orderByAsc(GoldPriceEntity::getTime);
		List<GoldPriceEntity> goldPriceEntities = this.goldPriceMapper.selectList(queryWrapper);
		GoldPriceResponse latestPrice = this.getLatestPrice();
		goldPriceEntities.add(BeanUtils.copy(latestPrice,GoldPriceEntity.class));
		List<List<Object>> resultList = new ArrayList<>();
		for (GoldPriceEntity goldPriceEntity : goldPriceEntities) {
			List<Object> list = new ArrayList<>();
			// 第一个是时间
			list.add(DateUtils.format(goldPriceEntity.getTime(),DateUtils.DATE_PATTERN));
			// 第二个是开盘价
			list.add(goldPriceEntity.getOpenPrice());
			// 第三个最新价
			list.add(goldPriceEntity.getLatestPrice());
			// 第四个是最低价
			list.add(goldPriceEntity.getMinPrice());
			// 第五个是最高价
			list.add(goldPriceEntity.getMaxPrice());
			resultList.add(list);
		}
		return resultList;
	}

}

