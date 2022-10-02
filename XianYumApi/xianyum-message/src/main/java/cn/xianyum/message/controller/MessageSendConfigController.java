package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.po.MessageSendConfigEntity;
import cn.xianyum.message.entity.request.MessageSendConfigRequest;
import cn.xianyum.message.entity.response.MessageSendConfigResponse;
import cn.xianyum.message.service.MessageSendConfigService;
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
 * 消息发送配置接口
 *
 */
@Api(tags = "消息发送配置接口")
@RestController
@RequestMapping(value = "/v1/messageSendConfig")
@Slf4j
public class MessageSendConfigController {

	@Autowired
	private MessageSendConfigService messageSendConfigService;

    /**
     * 消息发送配置分页查询数据
     *
     */
	@ApiOperation(value = "消息发送配置分页查询数据")
	@PostMapping(value = "/getPage")
    @Permissions(value = {"visitor","common"})
	public DataResult getPage(@RequestBody MessageSendConfigRequest request) {

		IPage<MessageSendConfigResponse> response = messageSendConfigService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 消息发送配置根据ID查询数据
     *
     */
    @ApiOperation(value = "消息发送配置根据ID查询数据")
    @PostMapping(value = "/getById")
    @Permissions(value = {"visitor","common"})
    public DataResult getById(@RequestBody MessageSendConfigRequest request) {

        MessageSendConfigResponse response = messageSendConfigService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 消息发送配置保存数据
	 *
     */
    @ApiOperation(value = "消息发送配置保存数据")
    @PostMapping(value = "/save")
    @Permissions(value = {"visitor","common"})
    public DataResult save(@RequestBody MessageSendConfigRequest request) {

		Integer count = messageSendConfigService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 消息发送配置修改数据
	 *
     */
    @ApiOperation(value = "消息发送配置修改数据")
    @PostMapping(value = "/update")
    @Permissions(value = {"visitor","common"})
    public DataResult update(@RequestBody MessageSendConfigRequest request) {

		Integer count = messageSendConfigService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 消息发送配置删除数据
	 *
     */
    @ApiOperation(value = "消息发送配置删除数据")
    @PostMapping(value = "/delete")
    @Permissions(value = {"visitor","common"})
    public DataResult delete(@RequestBody String[] ids) {

		messageSendConfigService.deleteById(ids);
	    return DataResult.success();
    }


    /**
     * 消息发送配置保存数据
     *
     */
    @ApiOperation(value = "消息发送配置保存数据")
    @PostMapping(value = "/saveOrUpdate")
    @Permissions(value = {"visitor","common"})
    public DataResult saveOrUpdate(@RequestBody MessageSendConfigRequest request) {

        MessageSendConfigEntity messageSendConfigEntity = messageSendConfigService.saveOrUpdate(request);
        return DataResult.success(messageSendConfigEntity);
    }
}