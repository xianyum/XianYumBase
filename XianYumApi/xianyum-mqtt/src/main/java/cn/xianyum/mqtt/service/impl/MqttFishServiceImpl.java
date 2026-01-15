package cn.xianyum.mqtt.service.impl;

import cn.xianyum.common.utils.BeanUtils;
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
        MqttFishEntity fishEntity = BeanUtils.copy(payload, MqttFishEntity.class);
        mqttFishMapper.insert(fishEntity);
        redisUtils.set(fishLatestDataRedisPrefix, JSONObject.toJSONString(fishEntity));
    }

    /**
     * 获取最新的实时数据
     *
     * @return
     */
    @Override
    public MqttFishResponse queryLatestData() {
        if(redisUtils.hasKey(fishLatestDataRedisPrefix)){
            return JSONObject.parseObject(redisUtils.getString(fishLatestDataRedisPrefix), MqttFishResponse.class);
        }
        return null;
    }
}

