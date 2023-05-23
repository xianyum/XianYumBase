package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageConfigWebhookRequest;
import cn.xianyum.message.entity.response.MessageConfigWebhookResponse;
import cn.xianyum.message.infra.sender.WebhookSender;
import cn.xianyum.message.service.MessageConfigWebhookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 账户配置webhook接口
 *
 */
@Api(tags = "账户配置webhook接口")
@RestController
@RequestMapping(value = "/v1/messageConfigWebhook")
@Slf4j
public class MessageConfigWebhookController {

	@Autowired
	private MessageConfigWebhookService messageConfigWebhookService;

    @Autowired
    private WebhookSender webhookSender;

    /**
     * 账户配置webhook分页查询数据
     *
     */
	@ApiOperation(value = "账户配置webhook分页查询数据")
	@PostMapping(value = "/getPage")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getPage(@RequestBody MessageConfigWebhookRequest request) {

		IPage<MessageConfigWebhookResponse> response = messageConfigWebhookService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 账户配置webhook根据ID查询数据
     *
     */
    @ApiOperation(value = "账户配置webhook根据ID查询数据")
    @PostMapping(value = "/getById")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult getById(@RequestBody MessageConfigWebhookRequest request) {

        MessageConfigWebhookResponse response = messageConfigWebhookService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 账户配置webhook保存数据
	 *
     */
    @ApiOperation(value = "账户配置webhook保存数据")
    @PostMapping(value = "/save")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult save(@RequestBody MessageConfigWebhookRequest request) {

		Integer count = messageConfigWebhookService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 账户配置webhook修改数据
	 *
     */
    @ApiOperation(value = "账户配置webhook修改数据")
    @PostMapping(value = "/update")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult update(@RequestBody MessageConfigWebhookRequest request) {

		Integer count = messageConfigWebhookService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 账户配置webhook删除数据
	 *
     */
    @ApiOperation(value = "账户配置webhook删除数据")
    @PostMapping(value = "/delete")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult delete(@RequestBody String[] ids) {

		messageConfigWebhookService.deleteById(ids);
	    return DataResult.success();
    }

    /**
     * webhook账户测试发送
     *
     */
    @ApiOperation(value = "webhook账户测试发送")
    @PostMapping(value = "/sendWebhook")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult sendWebhook(@RequestBody MessageSenderEntity request) {
        try {
            webhookSender.sendWebhook(request);
            return DataResult.success();
        }catch (Exception e){
            log.error("webhook账户测试发送异常. ",e);
            return DataResult.error(e.getMessage());
        }
    }
}
