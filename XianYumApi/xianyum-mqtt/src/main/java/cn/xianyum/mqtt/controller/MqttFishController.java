package cn.xianyum.mqtt.controller;

import cn.xianyum.common.utils.Results;
import cn.xianyum.mqtt.entity.response.MqttFishResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.xianyum.mqtt.service.MqttFishService;

/**
 * (MqttFish)Controller
 *
 * @author makejava
 * @since 2026-01-15 22:04:01
 */
@RestController
@RequestMapping("/xianyum-mqtt/v1/mqttFish")
@Tag(name = "接口")
public class MqttFishController{

    @Autowired
    private MqttFishService mqttFishService;


    @GetMapping("/queryLatestData")
    @Operation(summary = "获取最新的实时数据")
    public Results queryLatestData(){
        MqttFishResponse response = mqttFishService.queryLatestData();
        return Results.success(response);
    }

}
