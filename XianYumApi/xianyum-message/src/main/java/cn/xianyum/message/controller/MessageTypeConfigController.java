package cn.xianyum.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.OperTypeEnum;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.request.MessageTypeConfigRequest;
import cn.xianyum.message.entity.response.MessageTypeConfigResponse;
import cn.xianyum.message.service.MessageTypeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 消息类型配置表接口
 *
 */
@Tag(name = "消息类型配置表接口")
@RestController
@RequestMapping(value = "xym-message/v1/messageTypeConfig")
@Slf4j
@OperLog(module = "消息类型配置")
public class MessageTypeConfigController {

	@Autowired
	private MessageTypeConfigService messageTypeConfigService;

    /**
     * 消息类型配置表分页查询数据
     *
     */
	@Operation(summary = "消息类型配置表分页查询数据")
	@GetMapping(value = "/getPage")
    @SaCheckPermission("message:type-config:page")
	@OperLog(operType = OperTypeEnum.QUERY, desc = "分页查询消息类型配置")
	public Results<MessageTypeConfigResponse> getPage(MessageTypeConfigRequest request) {
        PageResponse<MessageTypeConfigResponse> response = messageTypeConfigService.getPage(request);
        return Results.page(response);
	}

    /**
     * 消息类型配置表根据ID查询数据
     *
     */
    @Operation(summary = "消息类型配置表根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @SaCheckPermission("message:type-config:query")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询消息类型配置")
    public Results<MessageTypeConfigResponse> getById(@PathVariable String id) {
        MessageTypeConfigResponse response = messageTypeConfigService.getById(id);
        return Results.success(response);
    }

    /**
     * 消息类型配置表保存数据
	 *
     */
    @Operation(summary = "消息类型配置表保存数据")
    @PostMapping(value = "/save")
    @SaCheckPermission("message:type-config:save")
    @OperLog(operType = OperTypeEnum.ADD, desc = "新增消息类型配置")
    public Results<?> save(@RequestBody MessageTypeConfigRequest request) {
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
    @Operation(summary = "消息类型配置表修改数据")
    @PutMapping(value = "/update")
    @SaCheckPermission("message:type-config:update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "修改消息类型配置")
    public Results<?> update(@RequestBody MessageTypeConfigRequest request) {
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
    @Operation(summary = "消息类型配置表删除数据")
    @DeleteMapping(value = "/delete")
    @SaCheckPermission("message:type-config:delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除消息类型配置")
    public Results<?> delete(@RequestBody String[] ids) {
		messageTypeConfigService.deleteById(ids);
	    return Results.success();
    }


    /**
     * 消息类型配置查询全量数据
     *
     */
    @Operation(summary = "消息类型配置查询全量数据")
    @GetMapping(value = "/getList")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "查询全部消息类型配置")
    public Results<List<MessageTypeConfigResponse>> getList() {
        List<MessageTypeConfigResponse> responseList = messageTypeConfigService.getList();
        return Results.success(responseList);
    }
}
