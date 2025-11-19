package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.extension.entity.request.RobotRequest;
import cn.xianyum.extension.entity.response.RobotResponse;
import cn.xianyum.extension.service.RobotService;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * @author zhangwei
 * @date 2025/7/8 22:37
 */

@RestController
@RequestMapping("xym-extension/v1/robot")
@Tag(name = "机器人接口")
@Slf4j
public class RobotController {

    @Resource
    private RobotService robotService;

    @Operation(summary = "机器人自动回复")
    @PostMapping(value = "/auto-reply")
    @Permission(publicApi = true)
    public RobotResponse autoReply(@RequestBody RobotRequest request) {
        log.info("机器人请求参数：{}", JSONObject.toJSONString(request));
        return robotService.autoReply(request.getContent());
    }
}
