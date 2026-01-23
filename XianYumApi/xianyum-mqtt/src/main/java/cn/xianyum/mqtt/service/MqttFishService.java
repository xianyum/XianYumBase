package cn.xianyum.mqtt.service;


import cn.xianyum.mqtt.entity.request.MqttFishRequest;
import cn.xianyum.mqtt.entity.response.MqttFishReportResponse;
import cn.xianyum.mqtt.entity.response.MqttFishResponse;

/**
 * (MqttFish)service层
 *
 * @author zhangwei
 * @since 2026-01-15 22:04:03
 */
public interface MqttFishService{


    /**
     * 同步设备上报的iot数据
     * @param payload
     */
    void syncIotData(String payload);

    /**
     * 获取最新的实时数据
     * @return
     */
    MqttFishResponse queryLatestData();

    /**
     * 获取折线图
     * @param request
     * @return
     */
    MqttFishReportResponse getReportLineData(MqttFishRequest request);

    /**
     * 获取IOT上报的总量
     * @return
     */
    Long queryTotalCount();
}
