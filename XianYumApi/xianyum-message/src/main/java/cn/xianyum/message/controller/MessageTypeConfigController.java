package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.request.MessageTypeConfigRequest;
import cn.xianyum.message.entity.response.MessageTypeConfigResponse;
import cn.xianyum.message.service.MessageTypeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 消息类型配置表接口
 *
 */
@Api(tags = "消息类型配置表接口")
@RestController
@RequestMapping(value = "/xianyum-message/v1/messageTypeConfig")
@Slf4j
public class MessageTypeConfigController {

	@Autowired
	private MessageTypeConfigService messageTypeConfigService;

    /**
     * 消息类型配置表分页查询数据
     *
     */
	@ApiOperation(value = "消息类型配置表分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
	public Results getPage(MessageTypeConfigRequest request) {

        PageResponse<MessageTypeConfigResponse> response = messageTypeConfigService.getPage(request);
        return Results.page(response);
	}

    /**
     * 消息类型配置表根据ID查询数据
     *
     */
    @ApiOperation(value = "消息类型配置表根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results getById(@PathVariable String id) {

        MessageTypeConfigResponse response = messageTypeConfigService.getById(id);
        return Results.success(response);
    }

    /**
     * 消息类型配置表保存数据
	 *
     */
    @ApiOperation(value = "消息类型配置表保存数据")
    @PostMapping(value = "/save")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results save(@RequestBody MessageTypeConfigRequest request) {

		Integer count = messageTypeConfigService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }

    /**
     * 消息类型配置表修改数据
	 *
     */
    @ApiOperation(value = "消息类型配置表修改数据")
    @PutMapping(value = "/update")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results update(@RequestBody MessageTypeConfigRequest request) {

		Integer count = messageTypeConfigService.update(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("修改失败");
    }

	/**
     * 消息类型配置表删除数据
	 *
     */
    @ApiOperation(value = "消息类型配置表删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results delete(@RequestBody String[] ids) {

		messageTypeConfigService.deleteById(ids);
	    return Results.success();
    }


    /**
     * 消息类型配置查询全量数据
     *
     */
    @ApiOperation(value = "消息类型配置查询全量数据")
    @GetMapping(value = "/getList")
    public Results getList() {
        List<MessageTypeConfigResponse> responseList = messageTypeConfigService.getList();
        return Results.success(responseList);
    }
}
