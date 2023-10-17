package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageConfigEmailRequest;
import cn.xianyum.message.entity.response.MessageConfigEmailResponse;
import cn.xianyum.message.infra.sender.EmailSender;
import cn.xianyum.message.service.MessageConfigEmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 账户配置email接口
 *
 */
@Api(tags = "账户配置email接口")
@RestController
@RequestMapping(value = "/xianyum-message/v1/messageConfigEmail")
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
	@ApiOperation(value = "账户配置email分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
	public Results getPage(MessageConfigEmailRequest request) {

        PageResponse<MessageConfigEmailResponse> response = messageConfigEmailService.getPage(request);
        return Results.page(response);
	}

    /**
     * 账户配置email根据ID查询数据
     *
     */
    @ApiOperation(value = "账户配置email根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results getById(@PathVariable String id) {

        MessageConfigEmailResponse response = messageConfigEmailService.getById(id);
        return Results.success(response);
    }

    /**
     * 账户配置email保存数据
	 *
     */
    @ApiOperation(value = "账户配置email保存数据")
    @PostMapping(value = "/save")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
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
    @ApiOperation(value = "账户配置email修改数据")
    @PutMapping(value = "/update")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
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
    @ApiOperation(value = "账户配置email删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results delete(@RequestBody String[] ids) {

		messageConfigEmailService.deleteById(ids);
	    return Results.success();
    }

    /**
     * 邮箱账户测试发送
     *
     */
    @ApiOperation(value = "邮箱账户测试发送")
    @PutMapping(value = "/sendEmail")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
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
