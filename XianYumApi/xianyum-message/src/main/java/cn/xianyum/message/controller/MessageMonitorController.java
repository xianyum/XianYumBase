package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Results;
import cn.xianyum.message.entity.request.MessageMonitorRequest;
import cn.xianyum.message.entity.response.MessageMonitorResponse;
import cn.xianyum.message.service.MessageMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息监控接口
 *
 */
@Api(tags = "消息监控接口")
@RestController
@RequestMapping(value = "/xianyum-message/v1/messageMonitor")
@Slf4j
public class MessageMonitorController {

	@Autowired
	private MessageMonitorService messageMonitorService;

    /**
     * 消息监控分页查询数据
     *
     */
	@ApiOperation(value = "消息监控分页查询数据")
	@GetMapping(value = "/getPage")
	public Results getPage(MessageMonitorRequest request) {

        PageResponse<MessageMonitorResponse> response = messageMonitorService.getPage(request);
        return Results.page(response);
	}

    /**
     * 消息监控根据ID查询数据
     *
     */
    @ApiOperation(value = "消息监控根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    @Permission(publicApi = true)
    public Results getById(@PathVariable String id) {
        MessageMonitorResponse response = messageMonitorService.getById(id);
        return Results.success(response);
    }

    /**
     * 消息监控保存数据
	 *
     */
    @ApiOperation(value = "消息监控保存数据")
    @PostMapping(value = "/save")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results save(@RequestBody MessageMonitorRequest request) {

		Integer count = messageMonitorService.save(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("保存失败");
    }

    /**
     * 消息监控修改数据
	 *
     */
    @ApiOperation(value = "消息监控修改数据")
    @PutMapping(value = "/update")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results update(@RequestBody MessageMonitorRequest request) {

		Integer count = messageMonitorService.update(request);
		if(count>0){
			return Results.success();
		}
		return Results.error("修改失败");
    }

	/**
     * 消息监控删除数据
	 *
     */
    @ApiOperation(value = "消息监控删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results delete(@RequestBody String[] ids) {
		messageMonitorService.deleteById(ids);
	    return Results.success();
    }


    /**
     * 清空监控删除数据
     *
     */
    @ApiOperation(value = "清空监控删除数据")
    @DeleteMapping(value = "/truncate")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results truncate() {
        messageMonitorService.truncate();
        return Results.success();
    }


    /**
     * 获取消息发送数量
     *
     */
    @ApiOperation(value = "获取消息发送数量")
    @GetMapping(value = "/getMessageLogCount")
    public Results getMessageLogCount() {
        return Results.success(messageMonitorService.getOnlineProxyCount());
    }
}
