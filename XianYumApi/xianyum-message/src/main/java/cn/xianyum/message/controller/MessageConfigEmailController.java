package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Result;
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
import com.baomidou.mybatisplus.core.metadata.IPage;

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
	public Result getPage(MessageConfigEmailRequest request) {

		IPage<MessageConfigEmailResponse> response = messageConfigEmailService.getPage(request);
        return Result.page(response);
	}

    /**
     * 账户配置email根据ID查询数据
     *
     */
    @ApiOperation(value = "账户配置email根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result getById(@PathVariable String id) {

        MessageConfigEmailResponse response = messageConfigEmailService.getById(id);
        return Result.success(response);
    }

    /**
     * 账户配置email保存数据
	 *
     */
    @ApiOperation(value = "账户配置email保存数据")
    @PostMapping(value = "/save")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result save(@RequestBody MessageConfigEmailRequest request) {

		Integer count = messageConfigEmailService.save(request);
		if(count>0){
			return Result.success();
		}
		return Result.error("保存失败");
    }

    /**
     * 账户配置email修改数据
	 *
     */
    @ApiOperation(value = "账户配置email修改数据")
    @PutMapping(value = "/update")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result update(@RequestBody MessageConfigEmailRequest request) {

		Integer count = messageConfigEmailService.update(request);
		if(count>0){
			return Result.success();
		}
		return Result.error("修改失败");
    }

	/**
     * 账户配置email删除数据
	 *
     */
    @ApiOperation(value = "账户配置email删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result delete(@RequestBody String[] ids) {

		messageConfigEmailService.deleteById(ids);
	    return Result.success();
    }

    /**
     * 邮箱账户测试发送
     *
     */
    @ApiOperation(value = "邮箱账户测试发送")
    @PutMapping(value = "/sendEmail")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result sendEmail(@RequestBody MessageSenderEntity request) {
        try {
            emailSender.sendEmail(request);
            return Result.success();
        }catch (Exception e){
            log.error("邮箱账户测试发送异常. ",e);
            return Result.error(e.getMessage());
        }
    }
}
