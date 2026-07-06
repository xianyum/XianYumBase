package cn.xianyum.message.controller;

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
@RequestMapping()
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/xym-message/v1/message/sendSimpleMessage")
    @Operation(summary = "发送简单消息")
    @Permission(publicApi = true)
    public Results<?> sendSimpleMessageByPost(@RequestParam() String messageCode, @RequestParam() String title, @RequestParam() String content){
        messageService.sendSimpleMessage(messageCode,title,content);
        return Results.success();
    }

    @PostMapping("/xym-message/v1/message/sendStandardMessage")
    @Operation(summary = "发送标准消息")
    @Permission(publicApi = true)
    public Results<?> sendStandardMessage(@RequestBody @Valid MessageSenderRequest messageSenderRequest){
        messageService.sendStandardMessage(messageSenderRequest);
        return Results.success();
    }

    @GetMapping("/xym-message/v1/message/sendSimpleMessage")
    @Operation(summary = "发送简单消息")
    @Permission(publicApi = true)
    public Results<?> sendSimpleMessageByGet(@RequestParam() String messageCode, @RequestParam() String title, @RequestParam() String content){
        messageService.sendSimpleMessage(messageCode,title,content);
        return Results.success();
    }

    /**
     * 主要接收飞牛os消息推送
     * @param request
     * @return
     */
    @Permission(publicApi = true)
    @PostMapping("/push")
    public Results<?> receiveFnOsPushMessage(@RequestBody FnOsPushMessageRequest request){
        messageService.receiveFnOsPushMessage(request);
        return Results.success();
    }

    @Permission(publicApi = true)
    @PostMapping("/grafana/alert")
    public Results<?> receiveGrafanaAlert(@RequestBody String json) {
        log.info("Grafana请求的日志,{}",json);
        GrafanaAlertWebhookRequest request =  JSONObject.parseObject(json, GrafanaAlertWebhookRequest.class);
        messageService.receiveGrafanaAlert(request);
        return Results.success();
    }
}
