package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.po.MessageSendConfigEntity;
import cn.xianyum.message.entity.request.MessageSendConfigRequest;
import cn.xianyum.message.entity.response.MessageSendConfigResponse;
import cn.xianyum.message.service.MessageSendConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息发送配置接口
 *
 */
@Tag(name = "消息发送配置接口")
@RestController
@RequestMapping(value = "xym-message/v1/messageSendConfig")
@Slf4j
public class MessageSendConfigController {

	@Autowired
	private MessageSendConfigService messageSendConfigService;

    /**
     * 消息发送配置分页查询数据
     *
     */
	@Operation(summary = "消息发送配置分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission("@ps.hasPerm('message:send-config:page')")
	public Results getPage(MessageSendConfigRequest request) {

        PageResponse<MessageSendConfigResponse> response = messageSendConfigService.getPage(request);
        return Results.page(response);
	}

    /**
     * 消息发送配置根据ID查询数据
     *
     */
    @Operation(summary = "消息发送配置根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission("@ps.hasPerm('message:send-config:query')")
    public Results getById(@PathVariable String id) {
        MessageSendConfigResponse response = messageSendConfigService.getById(id);
        return Results.success(response);
    }

    /**
     * 消息发送配置保存数据
	 *
     */
    @Operation(summary = "消息发送配置保存数据")
    @PostMapping(value = "/save")
    @Permission("@ps.hasPerm('message:send-config:save')")
    public Results save(@RequestBody MessageSendConfigRequest request) {

		Integer count = messageSendConfigService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }

    /**
     * 消息发送配置修改数据
	 *
     */
    @Operation(summary = "消息发送配置修改数据")
    @PutMapping(value = "/update")
    @Permission("@ps.hasPerm('message:send-config:update')")
    public Results update(@RequestBody MessageSendConfigRequest request) {

		Integer count = messageSendConfigService.update(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("修改失败");
    }

	/**
     * 消息发送配置删除数据
	 *
     */
    @Operation(summary = "消息发送配置删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('message:send-config:delete')")
    public Results delete(@RequestBody String[] ids) {

		messageSendConfigService.deleteById(ids);
	    return Results.success();
    }


    /**
     * 消息发送配置保存数据
     *
     */
    @Operation(summary = "消息发送配置保存数据")
    @PostMapping(value = "/saveOrUpdate")
    @Permission("@ps.hasPerm('message:send-config:save')")
    public Results saveOrUpdate(@RequestBody MessageSendConfigRequest request) {

        MessageSendConfigEntity messageSendConfigEntity = messageSendConfigService.saveOrUpdate(request);
        return Results.success(messageSendConfigEntity);
    }
}
