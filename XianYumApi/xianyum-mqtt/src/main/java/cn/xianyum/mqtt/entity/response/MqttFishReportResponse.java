package cn.xianyum.mqtt.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报表返回
 * @author xianyum
 * @date 2026/1/17 15:47
 */
@Data
public class MqttFishReportResponse {

    /**
     * x轴坐标数据
     */
    private List<String> xAxisDataList;

    /**
     * 室内温度
     */
    private List<BigDecimal> indoorTempList;

    /**
     * 鱼缸温度
     */
    private List<BigDecimal> fishTankTempList;


    /**
     * 室内湿度
     */
    private List<BigDecimal> indoorHumidityList;

    /**
     * 鱼缸TDS
     */
    private List<BigDecimal> fishTankTdsList;
}
