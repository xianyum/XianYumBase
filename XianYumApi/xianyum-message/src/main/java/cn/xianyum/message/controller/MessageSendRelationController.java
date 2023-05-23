package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.request.MessageSendRelationRequest;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import cn.xianyum.message.service.MessageSendRelationService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
 * 发送配置关联表接口
 *
 */
@Api(tags = "发送配置关联表接口")
@RestController
@RequestMapping(value = "/v1/messageSendRelation")
@Slf4j
public class MessageSendRelationController {

	@Autowired
	private MessageSendRelationService messageSendRelationService;

    /**
     * 发送配置关联表分页查询数据
     *
     */
	@ApiOperation(value = "发送配置关联表分页查询数据")
	@PostMapping(value = "/getPage")
	@Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getPage(@RequestBody MessageSendRelationRequest request) {

		IPage<MessageSendRelationResponse> response = messageSendRelationService.getPage(request);
        return DataResult.success(response);
	}

	/**
	 * 发送配置关联表删除数据
	 *
	 */
	@ApiOperation(value = "发送配置关联表删除数据")
	@PostMapping(value = "/delete")
	@Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult delete(@RequestBody MessageSendRelationRequest request) {

		messageSendRelationService.deleteById(request.getId());
		return DataResult.success();
	}

	/**
	 * 发送配置关联表保存数据
	 *
	 */
	@ApiOperation(value = "发送配置关联表保存数据")
	@PostMapping(value = "/save")
	@Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult save(@RequestBody MessageSendRelationRequest request) {

		Integer count = messageSendRelationService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
	}

	/**
	 * 发送配置关联表修改数据
	 *
	 */
	@ApiOperation(value = "发送配置关联表修改数据")
	@PostMapping(value = "/update")
	@Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult update(@RequestBody MessageSendRelationRequest request) {

		Integer count = messageSendRelationService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
	}


	/**
	 * 发送配置关联表根据ID查询数据
	 *
	 */
	@ApiOperation(value = "发送配置关联表根据ID查询数据")
	@PostMapping(value = "/getById")
	@Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getById(@RequestBody MessageSendRelationRequest request) {

		MessageSendRelationResponse response = messageSendRelationService.getById(request);
		return DataResult.success(response);
	}

	/**
	 * 根据账户类型查询账户配置
	 *
	 */
	@ApiOperation(value = "根据账户类型查询账户配置")
	@PostMapping(value = "/getMessageConfigByAccountType")
	@Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getMessageConfigByAccountType(@RequestBody MessageSendRelationRequest request) {

		JSONArray response = messageSendRelationService.getMessageConfigByAccountType(request.getMessageConfigId(),request.getMessageAccountType());
		return DataResult.success(response);
	}
}
