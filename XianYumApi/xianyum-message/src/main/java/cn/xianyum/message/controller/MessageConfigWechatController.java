package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
	@PostMapping(value = "/getPage")
    @Permissions(value = {"visitor","common"})
	public DataResult getPage(@RequestBody MessageConfigWechatRequest request) {

		IPage<MessageConfigWechatResponse> response = messageConfigWechatService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 账户配置wechat根据ID查询数据
     *
     */
    @ApiOperation(value = "账户配置wechat根据ID查询数据")
    @PostMapping(value = "/getById")
    @Permissions(value = {"visitor","common"})
    public DataResult getById(@RequestBody MessageConfigWechatRequest request) {

        MessageConfigWechatResponse response = messageConfigWechatService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 账户配置wechat保存数据
	 *
     */
    @ApiOperation(value = "账户配置wechat保存数据")
    @PostMapping(value = "/save")
    @Permissions(value = {"visitor","common"})
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
    @PostMapping(value = "/update")
    @Permissions(value = {"visitor","common"})
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
    @PostMapping(value = "/delete")
    @Permissions(value = {"visitor","common"})
    public DataResult delete(@RequestBody String[] ids) {

		messageConfigWechatService.deleteById(ids);
	    return DataResult.success();
    }

    /**
     * 企业微信账户测试发送
     *
     */
    @ApiOperation(value = "企业微信账户测试发送")
    @PostMapping(value = "/sendWechat")
    @Permissions(value = {"visitor","common"})
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
