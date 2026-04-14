package cn.xianyum.mqtt.service.impl;

import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.enums.TrendEnum;
import cn.xianyum.common.exception.SoException;
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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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

    @Autowired
    private ThreadPoolTaskExecutor xianYumTaskExecutor;


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
        boolean isHaveHourFirstData = redisUtils.hasKey(RedisKeyEnum.MQTT_FISH_PREVIOUS_DATA.getKey()+":"+nowHourStr);
        fishEntity.setFirstOfHour(!isHaveHourFirstData);
        String fishEntityJson = JSONObject.toJSONString(fishEntity);
        if(!isHaveHourFirstData){
            redisUtils.setMin(RedisKeyEnum.MQTT_FISH_PREVIOUS_DATA.getKey()+":"+nowHourStr, fishEntityJson,130);
        }
        redisUtils.set(RedisKeyEnum.MQTT_FISH_LATEST_DATA.getKey(), fishEntityJson);
        mqttFishMapper.insert(fishEntity);
    }

    /**
     * 获取最新的实时数据
     *
     * @return
     */
    @Override
    public MqttFishResponse queryLatestData() {
        if(!redisUtils.hasKey(RedisKeyEnum.MQTT_FISH_LATEST_DATA.getKey())){
            return null;
        }
        String previousNowHourStr = DateUtils.format(DateUtils.addDateHours(new Date(),-1), DateUtils.YYYY_MM_DD_HH_STR);
        boolean isHaveHourFirstData = redisUtils.hasKey(RedisKeyEnum.MQTT_FISH_PREVIOUS_DATA.getKey()+":"+previousNowHourStr);
        MqttFishResponse mqttFishResponse = JSONObject.parseObject(redisUtils.getString(RedisKeyEnum.MQTT_FISH_LATEST_DATA.getKey()), MqttFishResponse.class);
        if(Objects.isNull(mqttFishResponse)){
            return null;
        }
        if(isHaveHourFirstData){
            MqttFishResponse perViousMqttFishResponse = JSONObject.parseObject(redisUtils.getString(RedisKeyEnum.MQTT_FISH_PREVIOUS_DATA.getKey()+":"+previousNowHourStr), MqttFishResponse.class);
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
        // 缓存键
        String cacheKey = "XianYumApi:xianyum-mqtt:fish-ai:analysis";
        String processingKey = "XianYumApi:xianyum-mqtt:fish-ai:processing";

        // 尝试从Redis读取缓存
        String cachedAnalysis = redisUtils.getString(cacheKey);
        if (StringUtil.isNotBlank(cachedAnalysis)) {
            return cachedAnalysis;
        }

        // 检查是否正在处理中
        if (redisUtils.hasKey(processingKey)) {
            throw new SoException("AI正在分析中，请稍后查看结果");
        }

        // 设置处理中标志
        redisUtils.setMin(processingKey, true, 10);
        CompletableFuture.runAsync(() -> {
            // 获取近30条的数据
            List<MqttFishEntity> mqttFishEntities = this.mqttFishMapper.getHourlyLatestData();
            StringBuilder prompt = new StringBuilder();
            prompt.append("# 你是一位专业鱼缸水族分析师。我会提供最近一段时间的鱼缸监测数据进行分析\n");
            prompt.append("最近的鱼缸数据如下：\n");
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
            prompt.append("1. 分析温度、TDS的变化趋势，判断是否存在异常波动。\n");
            prompt.append("2. 结合鱼缸水温、TDS值，综合评估水质健康等级，注意鱼缸没有加热棒\n");
            prompt.append("3. 根据TDS趋势给出科学换水建议：换水量、换水时间、注意事项。\n");
            prompt.append("4. 结合西安目前的气候特点，给出针对性的鱼缸维护建议。\n");
            prompt.append("5. 全部内容严格使用清晰整洁的Markdown格式输出，分析报告开头必须要有分析时间范围，采集周期，鱼缸状态，报告生成时间等基本信息(不要提示数据条数)。\n");
            String content = chatClient.prompt().user(prompt.toString()).call().content();

            // 缓存结果到Redis，设置30分钟过期
            redisUtils.setMin(cacheKey, content, 30);
            // 清除处理中标志
            redisUtils.del(processingKey);
        },xianYumTaskExecutor);
        throw new SoException("AI正在分析中，请稍后查看结果");
    }

}
