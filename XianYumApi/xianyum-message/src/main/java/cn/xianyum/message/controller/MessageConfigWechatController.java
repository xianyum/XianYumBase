package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageConfigWechatRequest;
import cn.xianyum.message.entity.response.MessageConfigWechatResponse;
import cn.xianyum.message.infra.sender.WechatSender;
import cn.xianyum.message.service.MessageConfigWechatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 账户配置wechat接口
 *
 */
@Tag(name = "账户配置wechat接口")
@RestController
@RequestMapping(value = "xym-message/v1/messageConfigWechat")
@Slf4j
public class MessageConfigWechatController {

	@Autowired
	private MessageConfigWechatService messageConfigWechatService;

    @Autowired
    private WechatSender wechatSender;

    /**
     * 账户配置wechat分页查询数据
     *
     */
	@Operation(summary = "账户配置wechat分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission("@ps.hasPerm('message:wechat:page')")
	public Results<MessageConfigWechatResponse> getPage(MessageConfigWechatRequest request) {
        PageResponse<MessageConfigWechatResponse> response = messageConfigWechatService.getPage(request);
        return Results.page(response);
	}

    /**
     * 账户配置wechat根据ID查询数据
     *
     */
    @Operation(summary = "账户配置wechat根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission("@ps.hasPerm('message:wechat:query')")
    public Results<MessageConfigWechatResponse> getById(@PathVariable String id) {
        MessageConfigWechatResponse response = messageConfigWechatService.getById(id);
        return Results.success(response);
    }

    /**
     * 账户配置wechat保存数据
	 *
     */
    @Operation(summary = "账户配置wechat保存数据")
    @PostMapping(value = "/save")
    @Permission("@ps.hasPerm('message:wechat:save')")
    public Results<?> save(@RequestBody MessageConfigWechatRequest request) {

		Integer count = messageConfigWechatService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }

    /**
     * 账户配置wechat修改数据
	 *
     */
    @Operation(summary = "账户配置wechat修改数据")
    @PutMapping(value = "/update")
    @Permission("@ps.hasPerm('message:wechat:update')")
    public Results<?> update(@RequestBody MessageConfigWechatRequest request) {

		Integer count = messageConfigWechatService.update(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("修改失败");
    }

	/**
     * 账户配置wechat删除数据
	 *
     */
    @Operation(summary = "账户配置wechat删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('message:wechat:delete')")
    public Results<?> delete(@RequestBody String[] ids) {

		messageConfigWechatService.deleteById(ids);
	    return Results.success();
    }

    /**
     * 企业微信账户测试发送
     *
     */
    @Operation(summary = "企业微信账户测试发送")
    @PutMapping(value = "/sendWechat")
    @Permission("@ps.hasPerm('message:wechat:test-send')")
    public Results<?> sendWechat(@RequestBody MessageSenderEntity request) {
        try {
            wechatSender.sendWechat(request);
            return Results.success();
        }catch (Exception e){
            log.error("企业微信账户测试发送异常. ",e);
            return Results.error(e.getMessage());
        }
    }
}
