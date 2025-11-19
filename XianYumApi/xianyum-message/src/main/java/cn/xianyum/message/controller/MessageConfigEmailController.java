package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
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
    @Permission("@ps.hasPerm('message:email:page')")
	public Results getPage(MessageConfigEmailRequest request) {
        PageResponse<MessageConfigEmailResponse> response = messageConfigEmailService.getPage(request);
        return Results.page(response);
	}

    /**
     * 账户配置email根据ID查询数据
     *
     */
    @Operation(summary = "账户配置email根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission("@ps.hasPerm('message:email:query')")
    public Results getById(@PathVariable String id) {

        MessageConfigEmailResponse response = messageConfigEmailService.getById(id);
        return Results.success(response);
    }

    /**
     * 账户配置email保存数据
	 *
     */
    @Operation(summary = "账户配置email保存数据")
    @PostMapping(value = "/save")
    @Permission("@ps.hasPerm('message:email:save')")
    public Results save(@RequestBody MessageConfigEmailRequest request) {

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
    @Permission("@ps.hasPerm('message:email:update')")
    public Results update(@RequestBody MessageConfigEmailRequest request) {

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
    @Permission("@ps.hasPerm('message:email:delete')")
    public Results delete(@RequestBody String[] ids) {

		messageConfigEmailService.deleteById(ids);
	    return Results.success();
    }

    /**
     * 邮箱账户测试发送
     *
     */
    @Operation(summary = "邮箱账户测试发送")
    @PutMapping(value = "/sendEmail")
    @Permission("@ps.hasPerm('message:email:test-send')")
    public Results sendEmail(@RequestBody MessageSenderEntity request) {
        try {
            emailSender.sendEmail(request);
            return Results.success();
        }catch (Exception e){
            log.error("邮箱账户测试发送异常. ",e);
            return Results.error(e.getMessage());
        }
    }
}
