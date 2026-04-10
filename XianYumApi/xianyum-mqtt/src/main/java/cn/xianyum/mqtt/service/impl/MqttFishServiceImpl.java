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
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.mqtt.service.MqttFishService;
import cn.xianyum.mqtt.dao.MqttFishMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * (MqttFish)service层实现
 *
 * @author zhangwei
 * @since 2026-01-15 22:04:03
 */
@Service
@Slf4j
public class MqttFishServiceImpl implements MqttFishService {

	@Resource
	private MqttFishMapper mqttFishMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ChatClient chatClient;

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
        MqttFishReportResponse mqttFishReportResponse = new MqttFishReportResponse();
        List<BigDecimal> indoorTempList = new ArrayList<>();
        List<BigDecimal> fishTankTempList = new ArrayList<>();
        List<BigDecimal> indoorHumidityList = new ArrayList<>();
        List<BigDecimal> fishTankTdsList = new ArrayList<>();
        List<String> xAxisDataList = new ArrayList<>();
        Collections.reverse(mqttFishResponses);
        for (MqttFishResponse mqttFishResponse : mqttFishResponses) {
            xAxisDataList.add(mqttFishResponse.getDateStr());
            indoorTempList.add(Objects.nonNull(mqttFishResponse.getIndoorTemp())?mqttFishResponse.getIndoorTemp():BigDecimal.ZERO);
            fishTankTempList.add(Objects.nonNull(mqttFishResponse.getFishTankTemp())?mqttFishResponse.getFishTankTemp():BigDecimal.ZERO);
            fishTankTdsList.add(Objects.nonNull(mqttFishResponse.getFishTankTds())?mqttFishResponse.getFishTankTds():BigDecimal.ZERO);
            indoorHumidityList.add(Objects.nonNull(mqttFishResponse.getIndoorHumidity())?mqttFishResponse.getIndoorHumidity():BigDecimal.ZERO);
        }
        mqttFishReportResponse.setXAxisDataList(xAxisDataList);
        mqttFishReportResponse.setFishTankTempList(fishTankTempList);
        mqttFishReportResponse.setIndoorTempList(indoorTempList);
        mqttFishReportResponse.setFishTankTdsList(fishTankTdsList);
        mqttFishReportResponse.setIndoorHumidityList(indoorHumidityList);
        return mqttFishReportResponse;
    }

    /**
     * 获取IOT上报的总量
     *
     * @return
     */
    @Override
    public Long queryTotalCount() {
        return this.mqttFishMapper.queryTotalCount();
    }

    /**
     * 鱼缸AI预测分析
     *
     * @return
     */
    @Override
    public String aiAnalysis() {
        LambdaQueryWrapper<MqttFishEntity> queryWrapper = Wrappers.<MqttFishEntity>lambdaQuery()
                .orderByDesc(MqttFishEntity::getId).last("limit 30");
        // 获取近30条的数据
        List<MqttFishEntity> mqttFishEntities = this.mqttFishMapper.selectList(queryWrapper);
        StringBuilder prompt = new StringBuilder();
        prompt.append("# 你是一位专业鱼缸水族分析师。我会提供最近一段时间的鱼缸监测数据进行分析\n");
        prompt.append("最近的鱼缸数据如下：\n\n");
        // 格式化数据
        for (MqttFishEntity data : mqttFishEntities) {
            prompt.append("- 时间：").append(DateUtils.format(data.getCreateTime())).append("\n");
            prompt.append("- 室内温度：").append(data.getIndoorTemp()).append("°C\n");
            prompt.append("- 室内湿度：").append(data.getIndoorHumidity()).append("%\n");
            prompt.append("- 鱼缸温度：").append(data.getFishTankTemp()).append("°C\n");
            prompt.append("- 鱼缸TDS：").append(data.getFishTankTds()).append("\n\n");
        }

        prompt.append("## 分析背景\n");
        prompt.append("当前地点：中国西安新城区。\n");
        prompt.append("## 分析要求\n");
        prompt.append("1. 分析温度、TDS 的变化趋势，判断是否存在异常波动。\n");
        prompt.append("2. 结合鱼缸水温、TDS 值，综合评估水质健康等级。\n");
        prompt.append("3. 根据 TDS 趋势给出科学换水建议：换水量、换水时间、注意事项。\n");
        prompt.append("4. 结合西安干燥、温差大的气候特点，给出针对性的鱼缸维护建议。\n");
        prompt.append("5. 全部内容严格使用清晰整洁的 Markdown 格式输出\n");
        log.info("AI鱼缸提示词:{}",prompt);
        return chatClient.prompt().user(prompt.toString()).call().content();
    }

}

