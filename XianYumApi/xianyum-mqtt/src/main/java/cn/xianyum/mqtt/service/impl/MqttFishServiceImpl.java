package cn.xianyum.mqtt.service.impl;

import cn.xianyum.common.enums.TrendEnum;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.mqtt.entity.po.MqttFishEntity;
import cn.xianyum.mqtt.entity.request.MqttFishRequest;
import cn.xianyum.mqtt.entity.response.MqttFishReportResponse;
import cn.xianyum.mqtt.entity.response.MqttFishResponse;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.mqtt.service.MqttFishService;
import cn.xianyum.mqtt.dao.MqttFishMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * (MqttFish)service层实现
 *
 * @author makejava
 * @since 2026-01-15 22:04:03
 */
@Service
@Slf4j
public class MqttFishServiceImpl implements MqttFishService {


	@Resource
	private MqttFishMapper mqttFishMapper;

    @Resource
    private RedisUtils redisUtils;

    // 最新缓存数据
    @Value("${redis.mqtt.fish_latest_data}")
    private String fishLatestDataRedisPrefix;

    // 记录每个小时第一次上报的的mqtt数据
    @Value("${redis.mqtt.fish_previous_data}")
    private String fishPreviousDataRedisPrefix;

    /**
     * 同步设备上报的iot数据
     *
     * @param payload
     */
    @Override
    public void syncIotData(String payload) {
        if(StringUtil.isBlank(payload)){
            return;
        }
        MqttFishEntity fishEntity = JSONObject.parseObject(payload).toJavaObject(MqttFishEntity.class);
        Date nowDate = new Date();
        fishEntity.setCreateTime(nowDate);
        // 保存每个小时的第一条数据
        String nowHourStr = DateUtils.format(nowDate, DateUtils.YYYY_MM_DD_HH_STR);
        boolean isHaveHourFirstData = redisUtils.hasKey(fishPreviousDataRedisPrefix+":"+nowHourStr);
        fishEntity.setFirstOfHour(!isHaveHourFirstData);
        String fishEntityJson = JSONObject.toJSONString(fishEntity);
        if(!isHaveHourFirstData){
            redisUtils.setMin(fishPreviousDataRedisPrefix+":"+nowHourStr, fishEntityJson,130);
        }
        redisUtils.set(fishLatestDataRedisPrefix, fishEntityJson);
        mqttFishMapper.insert(fishEntity);
    }

    /**
     * 获取最新的实时数据
     *
     * @return
     */
    @Override
    public MqttFishResponse queryLatestData() {
        if(!redisUtils.hasKey(fishLatestDataRedisPrefix)){
            return null;
        }
        String previousNowHourStr = DateUtils.format(DateUtils.addDateHours(new Date(),-1), DateUtils.YYYY_MM_DD_HH_STR);
        boolean isHaveHourFirstData = redisUtils.hasKey(fishPreviousDataRedisPrefix+":"+previousNowHourStr);
        MqttFishResponse mqttFishResponse = JSONObject.parseObject(redisUtils.getString(fishLatestDataRedisPrefix), MqttFishResponse.class);
        if(Objects.isNull(mqttFishResponse)){
            return null;
        }
        if(isHaveHourFirstData){
            MqttFishResponse perViousMqttFishResponse = JSONObject.parseObject(redisUtils.getString(fishPreviousDataRedisPrefix+":"+previousNowHourStr), MqttFishResponse.class);
            mqttFishResponse.setFishTankTdsTrend(TrendEnum.judgeTrend(mqttFishResponse.getFishTankTds(),perViousMqttFishResponse.getFishTankTds()).getCode());
            mqttFishResponse.setFishTankTempTrend(TrendEnum.judgeTrend(mqttFishResponse.getFishTankTemp(),perViousMqttFishResponse.getFishTankTemp()).getCode());
            mqttFishResponse.setIndoorHumidityTrend(TrendEnum.judgeTrend(mqttFishResponse.getIndoorHumidity(),perViousMqttFishResponse.getIndoorHumidity()).getCode());
            mqttFishResponse.setIndoorTempTrend(TrendEnum.judgeTrend(mqttFishResponse.getIndoorTemp(),perViousMqttFishResponse.getIndoorTemp()).getCode());
        }
        return mqttFishResponse;
    }

    /**
     * 获取折线图
     *
     * @param request
     * @return
     */
    @Override
    public MqttFishReportResponse getReportLineData(MqttFishRequest request) {
        if(Objects.isNull(request.getDateType())){
            return new MqttFishReportResponse();
        }
        List<MqttFishResponse> mqttFishResponses = this.mqttFishMapper.getReportLineData(request);
        if(CollectionUtils.isEmpty(mqttFishResponses)){
            return new MqttFishReportResponse();
        }
        boolean isHour = request.getDateType().equals(0);
        List<String> dateStrings = isHour?DateUtils.getLast24Hours():DateUtils.minusDate(DateUtils.addDateDays(new Date(),1), DateUtils.DATE_PATTERN, 15);
        MqttFishReportResponse mqttFishReportResponse = new MqttFishReportResponse();
        List<BigDecimal> indoorTempList = new ArrayList<>();
        List<BigDecimal> fishTankTempList = new ArrayList<>();
        List<BigDecimal> indoorHumidityList = new ArrayList<>();
        List<BigDecimal> fishTankTdsList = new ArrayList<>();
        List<String> xAxisDataList = new ArrayList<>();
        Collections.reverse(dateStrings);
        for (String dateString : dateStrings) {
            xAxisDataList.add(dateString);
            Optional<MqttFishResponse> optionalMqttFishResponse = mqttFishResponses.stream().filter(item -> item.getDateStr().equals(dateString)).findFirst();
            if(optionalMqttFishResponse.isPresent()){
                MqttFishResponse mqttFishResponse = optionalMqttFishResponse.get();
                indoorTempList.add(mqttFishResponse.getIndoorHumidity());
                fishTankTempList.add(mqttFishResponse.getFishTankTemp());
                fishTankTdsList.add(mqttFishResponse.getFishTankTds());
                indoorHumidityList.add(mqttFishResponse.getIndoorHumidity());
            }else{
                indoorTempList.add(BigDecimal.ZERO);
                fishTankTempList.add(BigDecimal.ZERO);
                fishTankTdsList.add(BigDecimal.ZERO);
                indoorHumidityList.add(BigDecimal.ZERO);
            }
        }
        mqttFishReportResponse.setXAxisDataList(xAxisDataList);
        mqttFishReportResponse.setFishTankTempList(fishTankTempList);
        mqttFishReportResponse.setIndoorTempList(indoorTempList);
        mqttFishReportResponse.setFishTankTdsList(fishTankTdsList);
        mqttFishReportResponse.setIndoorHumidityList(indoorHumidityList);
        return mqttFishReportResponse;
    }

}

