package cn.xianyum.message.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.request.FnOsPushMessageRequest;
import cn.xianyum.message.entity.request.GrafanaAlertWebhookRequest;
import cn.xianyum.message.entity.request.MessageSenderRequest;
import cn.xianyum.message.service.MessageService;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @description
 * @date 2022/1/3 18:56
 */
@Tag(name = "消息接口")
@RestController
@RequestMapping(value = "xym-message/v1/message")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendSimpleMessage")
    @Operation(summary = "发送简单消息")
    @SaIgnore
    public Results<?> sendSimpleMessageByPost(@RequestParam() String messageCode, @RequestParam() String title, @RequestParam() String content){
        messageService.sendSimpleMessage(messageCode,title,content);
        return Results.success();
    }

    @PostMapping("/sendStandardMessage")
    @Operation(summary = "发送标准消息")
    @SaIgnore
    public Results<?> sendStandardMessage(@RequestBody @Valid MessageSenderRequest messageSenderRequest){
        messageService.sendStandardMessage(messageSenderRequest);
        return Results.success();
    }

    @GetMapping("/sendSimpleMessage")
    @Operation(summary = "发送简单消息")
    @SaIgnore
    public Results<?> sendSimpleMessageByGet(@RequestParam() String messageCode, @RequestParam() String title, @RequestParam() String content){
        messageService.sendSimpleMessage(messageCode,title,content);
        return Results.success();
    }

    /**
     * 主要接收飞牛os消息推送
     * @param request
     * @return
     */
    @Operation(summary = "接收飞牛os消息推送")
    @SaIgnore
    @PostMapping("/receiveFnOsPushMessage")
    public Results<?> receiveFnOsPushMessage(@RequestBody FnOsPushMessageRequest request){
        messageService.receiveFnOsPushMessage(request);
        return Results.success();
    }

    @SaIgnore
    @PostMapping("/grafana/alert")
    public Results<?> receiveGrafanaAlert(@RequestBody String json) {
        GrafanaAlertWebhookRequest request =  JSONObject.parseObject(json, GrafanaAlertWebhookRequest.class);
        messageService.receiveGrafanaAlert(request);
        return Results.success();
    }
}
