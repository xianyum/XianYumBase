package cn.xianyum.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.OperTypeEnum;
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
@OperLog(module = "企业微信配置")
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
    @SaCheckPermission("message:wechat:page")
	@OperLog(operType = OperTypeEnum.QUERY, desc = "分页查询企业微信配置")
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
    @SaCheckPermission("message:wechat:query")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询企业微信配置")
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
    @SaCheckPermission("message:wechat:save")
    @OperLog(operType = OperTypeEnum.ADD, desc = "新增企业微信配置")
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
    @SaCheckPermission("message:wechat:update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "修改企业微信配置")
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
    @SaCheckPermission("message:wechat:delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除企业微信配置")
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
    @SaCheckPermission("message:wechat:test-send")
    @OperLog(operType = OperTypeEnum.OTHER, desc = "企业微信测试发送")
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
