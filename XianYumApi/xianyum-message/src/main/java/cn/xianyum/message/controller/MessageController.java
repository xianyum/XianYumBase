package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.request.MessageSenderRequest;
import cn.xianyum.message.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *
 * @description
 * @date 2022/1/3 18:56
 */
@Api(tags = "消息接口")
@RestController
@RequestMapping("xianyum-message/v1/message")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendSimpleMessage")
    @ApiOperation(value = "发送简单消息")
    @Permission(publicApi = true)
    public Results sendSimpleMessage(@RequestParam() String messageCode, @RequestParam() String title, @RequestParam() String content){
        messageService.sendSimpleMessage(messageCode,title,content);
        return Results.success();
    }

    @PostMapping("/sendStandardMessage")
    @ApiOperation(value = "发送标准消息")
    @Permission(publicApi = true)
    public Results sendStandardMessage(@RequestBody @Valid MessageSenderRequest messageSenderRequest){
        messageService.sendStandardMessage(messageSenderRequest);
        return Results.success();
    }

}
