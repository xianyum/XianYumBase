package cn.xianyum.mqtt.service.impl;

import cn.xianyum.common.enums.TrendEnum;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.mqtt.entity.po.MqttFishEntity;
import cn.xianyum.mqtt.entity.response.MqttFishResponse;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.mqtt.service.MqttFishService;
import cn.xianyum.mqtt.dao.MqttFishMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Objects;

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

    // mqtt上报鱼的数据
    @Value("${redis.mqtt.data_prefix}")
    private String fishDataRedisPrefix;

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
        boolean isHaveHourFirstData = redisUtils.hHasKey(fishDataRedisPrefix, nowHourStr);
        fishEntity.setFirstOfHour(!isHaveHourFirstData);
        String fishEntityJson = JSONObject.toJSONString(fishEntity);
        if(!isHaveHourFirstData){
            redisUtils.hSet(fishDataRedisPrefix, nowHourStr,fishEntityJson);
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
        String nowHourStr = DateUtils.format(new Date(), DateUtils.YYYY_MM_DD_HH_STR);
        boolean isHaveHourFirstData = redisUtils.hHasKey(fishDataRedisPrefix, nowHourStr);
        MqttFishResponse mqttFishResponse = JSONObject.parseObject(redisUtils.getString(fishLatestDataRedisPrefix), MqttFishResponse.class);
        if(Objects.isNull(mqttFishResponse)){
            return null;
        }
        if(isHaveHourFirstData){
            MqttFishResponse perViousMqttFishResponse = JSONObject.parseObject((String) redisUtils.hGet(fishDataRedisPrefix,nowHourStr), MqttFishResponse.class);
            mqttFishResponse.setFishTankTdsTrend(TrendEnum.judgeTrend(mqttFishResponse.getFishTankTds(),perViousMqttFishResponse.getFishTankTds()).getCode());
            mqttFishResponse.setFishTankTempTrend(TrendEnum.judgeTrend(mqttFishResponse.getFishTankTemp(),perViousMqttFishResponse.getFishTankTemp()).getCode());
            mqttFishResponse.setIndoorHumidityTrend(TrendEnum.judgeTrend(mqttFishResponse.getIndoorHumidity(),perViousMqttFishResponse.getIndoorHumidity()).getCode());
            mqttFishResponse.setIndoorTempTrend(TrendEnum.judgeTrend(mqttFishResponse.getIndoorTemp(),perViousMqttFishResponse.getIndoorTemp()).getCode());
        }
        return mqttFishResponse;
    }
}

