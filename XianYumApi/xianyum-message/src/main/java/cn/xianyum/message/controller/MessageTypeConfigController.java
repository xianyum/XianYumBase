package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Result;
import cn.xianyum.message.entity.request.MessageTypeConfigRequest;
import cn.xianyum.message.entity.response.MessageTypeConfigResponse;
import cn.xianyum.message.service.MessageTypeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

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
	public Result getPage(MessageTypeConfigRequest request) {

		IPage<MessageTypeConfigResponse> response = messageTypeConfigService.getPage(request);
        return Result.page(response);
	}

    /**
     * 消息类型配置表根据ID查询数据
     *
     */
    @ApiOperation(value = "消息类型配置表根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result getById(@PathVariable String id) {

        MessageTypeConfigResponse response = messageTypeConfigService.getById(id);
        return Result.success(response);
    }

    /**
     * 消息类型配置表保存数据
	 *
     */
    @ApiOperation(value = "消息类型配置表保存数据")
    @PostMapping(value = "/save")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result save(@RequestBody MessageTypeConfigRequest request) {

		Integer count = messageTypeConfigService.save(request);
		if(count>0){
			return Result.success();
		}
		return Result.error("保存失败");
    }

    /**
     * 消息类型配置表修改数据
	 *
     */
    @ApiOperation(value = "消息类型配置表修改数据")
    @PutMapping(value = "/update")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result update(@RequestBody MessageTypeConfigRequest request) {

		Integer count = messageTypeConfigService.update(request);
		if(count>0){
			return Result.success();
		}
		return Result.error("修改失败");
    }

	/**
     * 消息类型配置表删除数据
	 *
     */
    @ApiOperation(value = "消息类型配置表删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Result delete(@RequestBody String[] ids) {

		messageTypeConfigService.deleteById(ids);
	    return Result.success();
    }


    /**
     * 消息类型配置查询全量数据
     *
     */
    @ApiOperation(value = "消息类型配置查询全量数据")
    @GetMapping(value = "/getList")
    public Result getList() {
        List<MessageTypeConfigResponse> responseList = messageTypeConfigService.getList();
        return Result.success(responseList);
    }
}
