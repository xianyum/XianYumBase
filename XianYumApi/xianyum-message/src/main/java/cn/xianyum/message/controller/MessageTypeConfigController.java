package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.request.MessageTypeConfigRequest;
import cn.xianyum.message.entity.response.MessageTypeConfigResponse;
import cn.xianyum.message.service.MessageTypeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 消息类型配置表接口
 *
 */
@Api(tags = "消息类型配置表接口")
@RestController
@RequestMapping(value = "/v1/messageTypeConfig")
@Slf4j
public class MessageTypeConfigController {

	@Autowired
	private MessageTypeConfigService messageTypeConfigService;

    /**
     * 消息类型配置表分页查询数据
     *
     */
	@ApiOperation(value = "消息类型配置表分页查询数据")
	@PostMapping(value = "/getPage")
    @Permissions(value = {"visitor","common"})
	public DataResult getPage(@RequestBody MessageTypeConfigRequest request) {

		IPage<MessageTypeConfigResponse> response = messageTypeConfigService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 消息类型配置表根据ID查询数据
     *
     */
    @ApiOperation(value = "消息类型配置表根据ID查询数据")
    @PostMapping(value = "/getById")
    @Permissions(value = {"visitor","common"})
    public DataResult getById(@RequestBody MessageTypeConfigRequest request) {

        MessageTypeConfigResponse response = messageTypeConfigService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 消息类型配置表保存数据
	 *
     */
    @ApiOperation(value = "消息类型配置表保存数据")
    @PostMapping(value = "/save")
    @Permissions(value = {"visitor","common"})
    public DataResult save(@RequestBody MessageTypeConfigRequest request) {

		Integer count = messageTypeConfigService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 消息类型配置表修改数据
	 *
     */
    @ApiOperation(value = "消息类型配置表修改数据")
    @PostMapping(value = "/update")
    @Permissions(value = {"visitor","common"})
    public DataResult update(@RequestBody MessageTypeConfigRequest request) {

		Integer count = messageTypeConfigService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 消息类型配置表删除数据
	 *
     */
    @ApiOperation(value = "消息类型配置表删除数据")
    @PostMapping(value = "/delete")
    @Permissions(value = {"visitor","common"})
    public DataResult delete(@RequestBody String[] ids) {

		messageTypeConfigService.deleteById(ids);
	    return DataResult.success();
    }


    /**
     * 消息类型配置表删除数据
     *
     */
    @ApiOperation(value = "消息类型配置查询全量数据")
    @PostMapping(value = "/getList")
    @Permissions(value = {"visitor","common"})
    public DataResult getList() {
        List<MessageTypeConfigResponse> responseList = messageTypeConfigService.getList();
        return DataResult.success(responseList);
    }
}
