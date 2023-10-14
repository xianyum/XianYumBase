package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @description
 * @date 2022/1/3 18:56
 */
@Api(tags = "消息接口")
@RestController
@RequestMapping("/xianyum-message/v1/message")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendSimpleMessage")
    @ApiOperation(value = "发送简单消息")
    @Permission(publicApi = true)
    public DataResult sendSimpleMessageForPost(@RequestParam() String messageCode, @RequestParam() String title, @RequestParam() String content){
        messageService.sendSimpleMessage(messageCode,title,content);
        return DataResult.success();
    }

    @GetMapping("/sendSimpleMessage")
    @ApiOperation(value = "发送简单消息")
    @Permission(publicApi = true)
    public DataResult sendSimpleMessageForGet(@RequestParam() String messageCode, @RequestParam() String title, @RequestParam() String content){
        messageService.sendSimpleMessage(messageCode,title,content);
        return DataResult.success();
    }

}
