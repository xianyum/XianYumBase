package cn.xianyum.mqtt.service.impl;

import cn.xianyum.common.enums.RedisKeyEnum;
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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.ai.openai.chat.options.model}")
    private String aiModel;

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
        // 最近一次换水时间
        mqttFishResponse.setWaterChangeLastTime(redisUtils.getString(RedisKeyEnum.MQTT_FISH_WATER_CHANGE_TIME.getKey()));
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
        String cacheKey = RedisKeyEnum.MQTT_FISH_AI_ANALYSIS.getKey();
        String processingKey = RedisKeyEnum.MQTT_FISH_AI_PROCESSING.getKey();

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
            prompt.append("# 你是一位资深水族环境分析师，精通鱼缸水质监测、水温管理、TDS评估、换水方案与鱼缸维护指导，分析专业、严谨、通俗易懂。我会提供最近一段时间的鱼缸监测数据进行分析\n");
            prompt.append("## 分析基础信息\n");
            prompt.append("- 监测地点：中国 陕西省 西安市 新城区\n");
            prompt.append("- 设备状态：无加热棒\n\n");
            prompt.append("- 设备状态：3条金鱼8条天使鱼2条蝶翼斑马鱼\n\n");
            prompt.append("## 近期鱼缸监测数据\n");
            // 格式化数据
            for (MqttFishEntity data : mqttFishEntities) {
                prompt.append("- 时间：").append(DateUtils.format(data.getCreateTime())).append("\n");
                prompt.append("- 室内温度：").append(data.getIndoorTemp()).append("°C\n");
                prompt.append("- 室内湿度：").append(data.getIndoorHumidity()).append("%\n");
                prompt.append("- 鱼缸温度：").append(data.getFishTankTemp()).append("°C\n");
                prompt.append("- 鱼缸TDS：").append(data.getFishTankTds()).append("\n\n");
            }
            prompt.append("## 分析要求\n");
            prompt.append("1. 基础信息展示，报告开头必须按顺序包含：AI模型，分析时间范围、数据采集周期、鱼缸当前状态、报告生成时间，格式清晰。其中报告生成时间取："+DateUtils.format(new Date())+",AI模型取："+aiModel+",数据采集周期每隔5分钟上报一次。\n\n");
            if(redisUtils.hasKey(RedisKeyEnum.MQTT_FISH_WATER_CHANGE_TIME.getKey())){
                prompt.append("   - 最近一次换水时间："+redisUtils.getString(RedisKeyEnum.MQTT_FISH_WATER_CHANGE_TIME.getKey())+"\n");
            }
            prompt.append("2. 分析室内温度、鱼缸水温、TDS的变化趋势图，判断是否存在异常波动、骤升骤降。\n");
            prompt.append("3. 结合无加热棒的环境，综合评估水质健康等级（优秀/良好/一般/较差）。\n");
            prompt.append("4. 根据TDS变化趋势，以及最近一次换水时间，给出科学换水建议：建议换水量、最佳换水时间、换水注意事项。\n");
            prompt.append("5. 结合西安新城区当前气候特点（温差、干燥、室温波动），给出针对性鱼缸维护建议。\n");
            prompt.append("6. 输出格式规范：全程使用标准Markdown格式，优先使用表格呈现数据，合理使用emoji提升可读性，排版整洁、层级分明、无冗余内容\n");
            String content = chatClient.prompt().user(prompt.toString()).call().content();

            // 缓存结果到Redis，设置30分钟过期
            redisUtils.setMin(cacheKey, content, 30);
            // 清除处理中标志
            redisUtils.del(processingKey);
        },xianYumTaskExecutor);
        throw new SoException("AI正在分析中，请稍后查看结果");
    }

    /**
     * 记录换水时间
     */
    @Override
    public void recordWaterChangeTime() {
        // 记录当前时间到Redis
        redisUtils.set(RedisKeyEnum.MQTT_FISH_WATER_CHANGE_TIME.getKey(), DateUtils.format(new Date()));
    }

}
