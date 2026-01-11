package cn.xianyum.mqtt.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.Results;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xianyum
 * @date 2026/1/11 0:59
 */
@RestController
@RequestMapping("xym-iot/v1/test")
@Tag(name = "测试")
@Slf4j
public class TestController {

    @Resource
    private RedisUtils redisUtils;
    @GetMapping("/test")
    public Results test(@RequestParam Integer day){
        return Results.success();
    }
}
