package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.MessageConfigWechatRequest;
import cn.xianyum.message.entity.response.MessageConfigWechatResponse;
import cn.xianyum.message.infra.sender.WechatSender;
import cn.xianyum.message.service.MessageConfigWechatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 账户配置wechat接口
 *
 */
@Api(tags = "账户配置wechat接口")
@RestController
@RequestMapping(value = "/v1/messageConfigWechat")
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
	@ApiOperation(value = "账户配置wechat分页查询数据")
	@GetMapping(value = "/getPage")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getPage(MessageConfigWechatRequest request) {

		IPage<MessageConfigWechatResponse> response = messageConfigWechatService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 账户配置wechat根据ID查询数据
     *
     */
    @ApiOperation(value = "账户配置wechat根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult getById(@PathVariable String id) {
        MessageConfigWechatResponse response = messageConfigWechatService.getById(id);
        return DataResult.success(response);
    }

    /**
     * 账户配置wechat保存数据
	 *
     */
    @ApiOperation(value = "账户配置wechat保存数据")
    @PostMapping(value = "/save")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult save(@RequestBody MessageConfigWechatRequest request) {

		Integer count = messageConfigWechatService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 账户配置wechat修改数据
	 *
     */
    @ApiOperation(value = "账户配置wechat修改数据")
    @PutMapping(value = "/update")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult update(@RequestBody MessageConfigWechatRequest request) {

		Integer count = messageConfigWechatService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 账户配置wechat删除数据
	 *
     */
    @ApiOperation(value = "账户配置wechat删除数据")
    @DeleteMapping(value = "/delete")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult delete(@RequestBody String[] ids) {

		messageConfigWechatService.deleteById(ids);
	    return DataResult.success();
    }

    /**
     * 企业微信账户测试发送
     *
     */
    @ApiOperation(value = "企业微信账户测试发送")
    @PutMapping(value = "/sendWechat")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult sendWechat(@RequestBody MessageSenderEntity request) {
        try {
            wechatSender.sendWechat(request);
            return DataResult.success();
        }catch (Exception e){
            log.error("企业微信账户测试发送异常. ",e);
            return DataResult.error(e.getMessage());
        }
    }
}
