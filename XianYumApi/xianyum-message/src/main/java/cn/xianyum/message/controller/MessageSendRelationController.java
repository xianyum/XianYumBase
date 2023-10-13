package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.request.MessageSendRelationRequest;
import cn.xianyum.message.entity.response.MessageSendRelationResponse;
import cn.xianyum.message.service.MessageSendRelationService;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 发送配置关联表接口
 *
 */
@Api(tags = "发送配置关联表接口")
@RestController
@RequestMapping(value = "/xianyum-message/v1/messageSendRelation")
@Slf4j
public class MessageSendRelationController {

	@Autowired
	private MessageSendRelationService messageSendRelationService;

    /**
     * 发送配置关联表分页查询数据
     *
     */
	@ApiOperation(value = "发送配置关联表分页查询数据")
	@GetMapping(value = "/getPage")
	@Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getPage(MessageSendRelationRequest request) {

		IPage<MessageSendRelationResponse> response = messageSendRelationService.getPage(request);
        return DataResult.success(response);
	}

	/**
	 * 发送配置关联表删除数据
	 *
	 */
	@ApiOperation(value = "发送配置关联表删除数据")
	@DeleteMapping(value = "/delete/{id}")
	@Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult delete(@PathVariable String id) {
		messageSendRelationService.deleteById(id);
		return DataResult.success();
	}

	/**
	 * 发送配置关联表保存数据
	 *
	 */
	@ApiOperation(value = "发送配置关联表保存数据")
	@PostMapping(value = "/save")
	@Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
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
	@PutMapping(value = "/update")
	@Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
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
	@GetMapping(value = "/getById/{id}")
	@Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getById(@PathVariable String id) {
		MessageSendRelationResponse response = messageSendRelationService.getById(id);
		return DataResult.success(response);
	}

	/**
	 * 根据账户类型查询账户配置
	 *
	 */
	@ApiOperation(value = "根据账户类型查询账户配置")
	@PostMapping(value = "/getMessageConfigByAccountType")
	@Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getMessageConfigByAccountType(@RequestBody MessageSendRelationRequest request) {

		JSONArray response = messageSendRelationService.getMessageConfigByAccountType(request.getMessageConfigId(),request.getMessageAccountType());
		return DataResult.success(response);
	}
}
