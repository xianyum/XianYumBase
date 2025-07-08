package cn.xianyum.extension.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.extension.entity.request.RobotRequest;
import cn.xianyum.extension.entity.request.ServerConfigRequest;
import cn.xianyum.extension.entity.response.RobotResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangwei
 * @date 2025/7/8 22:37
 */

@RestController
@RequestMapping("xym-extension/v1/robot")
@Api(tags = "机器人接口")
public class RobotController {

    @ApiOperation(value = "机器人自动回复")
    @PostMapping(value = "/auto-reply")
    @Permission(publicApi = true)
    public RobotResponse autoReply(@RequestBody RobotRequest request) {
        RobotResponse response = new RobotResponse();
        response.setReplyContent(request.getContent());
        return response;
    }
}
