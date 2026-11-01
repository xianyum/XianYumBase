package cn.xianyum.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.OperTypeEnum;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageConfigWebhookRequest;
import cn.xianyum.message.entity.response.MessageConfigWebhookResponse;
import cn.xianyum.message.infra.core.MessageFactory;
import cn.xianyum.message.service.MessageConfigWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 账户配置webhook接口
 *
 */
@Tag(name = "账户配置webhook接口")
@RestController
@RequestMapping(value = "xym-message/v1/messageConfigWebhook")
@Slf4j
@OperLog(module = "Webhook配置")
public class MessageConfigWebhookController {

	@Autowired
	private MessageConfigWebhookService messageConfigWebhookService;

    @Autowired
    private MessageFactory messageFactory;

    /**
     * 账户配置webhook分页查询数据
     *
     */
	@Operation(summary = "账户配置webhook分页查询数据")
	@GetMapping(value = "/getPage")
    @SaCheckPermission("message:webhook:page")
	@OperLog(operType = OperTypeEnum.QUERY, desc = "分页查询Webhook配置")
	public Results<MessageConfigWebhookResponse> getPage(MessageConfigWebhookRequest request) {
        PageResponse<MessageConfigWebhookResponse> response = messageConfigWebhookService.getPage(request);
        return Results.page(response);
	}

    /**
     * 账户配置webhook根据ID查询数据
     *
     */
    @Operation(summary = "账户配置webhook根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @SaCheckPermission("message:webhook:query")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询Webhook配置")
    public Results<MessageConfigWebhookResponse> getById(@PathVariable String id) {

        MessageConfigWebhookResponse response = messageConfigWebhookService.getById(id);
        return Results.success(response);
    }

    /**
     * 账户配置webhook保存数据
	 *
     */
    @Operation(summary = "账户配置webhook保存数据")
    @PostMapping(value = "/save")
    @SaCheckPermission("message:webhook:save")
    @OperLog(operType = OperTypeEnum.ADD, desc = "新增Webhook配置")
    public Results<?> save(@RequestBody MessageConfigWebhookRequest request) {

		Integer count = messageConfigWebhookService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }

    /**
     * 账户配置webhook修改数据
	 *
     */
    @Operation(summary = "账户配置webhook修改数据")
    @PutMapping(value = "/update")
    @SaCheckPermission("message:webhook:update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "修改Webhook配置")
    public Results<?> update(@RequestBody MessageConfigWebhookRequest request) {

		Integer count = messageConfigWebhookService.update(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("修改失败");
    }

	/**
     * 账户配置webhook删除数据
	 *
     */
    @Operation(summary = "账户配置webhook删除数据")
    @DeleteMapping(value = "/delete")
    @SaCheckPermission("message:webhook:delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除Webhook配置")
    public Results<?> delete(@RequestBody String[] ids) {

		messageConfigWebhookService.deleteById(ids);
	    return Results.success();
    }

    /**
     * webhook账户测试发送
     *
     */
    @Operation(summary = "webhook账户测试发送")
    @PutMapping(value = "/sendWebhook")
    @SaCheckPermission("message:webhook:test-send")
    @OperLog(operType = OperTypeEnum.OTHER, desc = "Webhook测试发送")
    public Results<?> sendWebhook(@RequestBody MessageSenderEntity request) {
        try {
            messageFactory.getMessageService(request.getMessageAccountType()).doSendMessage(request);
            return Results.success();
        }catch (Exception e){
            log.error("webhook账户测试发送异常. ",e);
            return Results.error(e.getMessage());
        }
    }
}
