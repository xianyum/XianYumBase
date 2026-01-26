package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
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
    @Permission("@ps.hasPerm('message:webhook:page')")
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
    @Permission("@ps.hasPerm('message:webhook:query')")
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
    @Permission("@ps.hasPerm('message:webhook:save')")
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
    @Permission("@ps.hasPerm('message:webhook:update')")
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
    @Permission("@ps.hasPerm('message:webhook:delete')")
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
    @Permission("@ps.hasPerm('message:webhook:test-send')")
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
