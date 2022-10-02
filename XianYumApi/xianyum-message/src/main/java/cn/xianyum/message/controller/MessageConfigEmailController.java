package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageConfigEmailRequest;
import cn.xianyum.message.entity.response.MessageConfigEmailResponse;
import cn.xianyum.message.infra.sender.EmailSender;
import cn.xianyum.message.service.MessageConfigEmailService;
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
 * 账户配置email接口
 *
 */
@Api(tags = "账户配置email接口")
@RestController
@RequestMapping(value = "/v1/messageConfigEmail")
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
	@PostMapping(value = "/getPage")
    @Permissions(value = {"visitor","common"})
	public DataResult getPage(@RequestBody MessageConfigEmailRequest request) {

		IPage<MessageConfigEmailResponse> response = messageConfigEmailService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 账户配置email根据ID查询数据
     *
     */
    @ApiOperation(value = "账户配置email根据ID查询数据")
    @PostMapping(value = "/getById")
    @Permissions(value = {"visitor","common"})
    public DataResult getById(@RequestBody MessageConfigEmailRequest request) {

        MessageConfigEmailResponse response = messageConfigEmailService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 账户配置email保存数据
	 *
     */
    @ApiOperation(value = "账户配置email保存数据")
    @PostMapping(value = "/save")
    @Permissions(value = {"visitor","common"})
    public DataResult save(@RequestBody MessageConfigEmailRequest request) {

		Integer count = messageConfigEmailService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 账户配置email修改数据
	 *
     */
    @ApiOperation(value = "账户配置email修改数据")
    @PostMapping(value = "/update")
    @Permissions(value = {"visitor","common"})
    public DataResult update(@RequestBody MessageConfigEmailRequest request) {

		Integer count = messageConfigEmailService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 账户配置email删除数据
	 *
     */
    @ApiOperation(value = "账户配置email删除数据")
    @PostMapping(value = "/delete")
    @Permissions(value = {"visitor","common"})
    public DataResult delete(@RequestBody String[] ids) {

		messageConfigEmailService.deleteById(ids);
	    return DataResult.success();
    }

    /**
     * 邮箱账户测试发送
     *
     */
    @ApiOperation(value = "邮箱账户测试发送")
    @PostMapping(value = "/sendEmail")
    @Permissions(value = {"visitor","common"})
    public DataResult sendEmail(@RequestBody MessageSenderEntity request) {
        try {
            emailSender.sendEmail(request);
            return DataResult.success();
        }catch (Exception e){
            log.error("邮箱账户测试发送异常. ",e);
            return DataResult.error(e.getMessage());
        }
    }
}