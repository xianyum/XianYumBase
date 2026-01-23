package cn.xianyum.mqtt.dao;

import cn.xianyum.mqtt.entity.request.MqttFishRequest;
import cn.xianyum.mqtt.entity.response.MqttFishResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.xianyum.mqtt.entity.po.MqttFishEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (MqttFish)表数据库访问层
 *
 * @author zhangwei
 * @since 2026-01-15 22:04:04
 */
public interface MqttFishMapper extends BaseMapper<MqttFishEntity> {

    List<MqttFishResponse> getReportLineData(@Param("request") MqttFishRequest request);

    Long queryTotalCount();
}

