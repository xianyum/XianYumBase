package cn.xianyum.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.OperTypeEnum;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageConfigEmailRequest;
import cn.xianyum.message.entity.response.MessageConfigEmailResponse;
import cn.xianyum.message.infra.sender.EmailSender;
import cn.xianyum.message.service.MessageConfigEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 账户配置email接口
 *
 */
@Tag(name = "账户配置email接口")
@RestController
@RequestMapping(value = "xym-message/v1/messageConfigEmail")
@Slf4j
@OperLog(module = "邮箱配置")
public class MessageConfigEmailController {

	@Autowired
	private MessageConfigEmailService messageConfigEmailService;

    @Autowired
    private EmailSender emailSender;

    /**
     * 账户配置email分页查询数据
     *
     */


	@GetMapping(value = "/getPage")
    @SaCheckPermission("message:email:page")
	public Results<MessageConfigEmailResponse> getPage(MessageConfigEmailRequest request) {
        PageResponse<MessageConfigEmailResponse> response = messageConfigEmailService.getPage(request);
        return Results.page(response);
	}

    /**
     * 账户配置email根据ID查询数据
     *
     */
    @Operation(summary = "账户配置email根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @SaCheckPermission("message:email:query")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询邮箱配置")
    public Results<MessageConfigEmailResponse> getById(@PathVariable String id) {
        MessageConfigEmailResponse response = messageConfigEmailService.getById(id);
        return Results.success(response);
    }

    /**
     * 账户配置email保存数据
	 *
     */
    @Operation(summary = "账户配置email保存数据")
    @PostMapping(value = "/save")
    @SaCheckPermission("message:email:save")
    @OperLog(operType = OperTypeEnum.ADD, desc = "新增邮箱配置")
    public Results<?> save(@RequestBody MessageConfigEmailRequest request) {

		Integer count = messageConfigEmailService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }

    /**
     * 账户配置email修改数据
	 *
     */
    @Operation(summary = "账户配置email修改数据")
    @PutMapping(value = "/update")
    @SaCheckPermission("message:email:update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "修改邮箱配置")
    public Results<?> update(@RequestBody MessageConfigEmailRequest request) {

		Integer count = messageConfigEmailService.update(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("修改失败");
    }

	/**
     * 账户配置email删除数据
	 *
     */
    @Operation(summary = "账户配置email删除数据")
    @DeleteMapping(value = "/delete")
    @SaCheckPermission("message:email:delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除邮箱配置")
    public Results<?> delete(@RequestBody String[] ids) {

		messageConfigEmailService.deleteById(ids);
	    return Results.success();
    }

    /**
     * 邮箱账户测试发送
     *
     */
    @Operation(summary = "邮箱账户测试发送")
    @PutMapping(value = "/sendEmail")
    @SaCheckPermission("message:email:test-send")
    @OperLog(operType = OperTypeEnum.OTHER, desc = "邮箱测试发送")
    public Results<?> sendEmail(@RequestBody MessageSenderEntity request) {
        try {
            emailSender.sendEmail(request);
            return Results.success();
        }catch (Exception e){
            log.error("邮箱账户测试发送异常. ",e);
            return Results.error(e.getMessage());
        }
    }
}
