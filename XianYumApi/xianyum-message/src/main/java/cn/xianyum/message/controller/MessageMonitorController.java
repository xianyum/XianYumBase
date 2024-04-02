package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
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
@RequestMapping(value = "xym-message/v1/messageMonitor")
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
    @Permission(value = "@ps.hasPerm('message:monitor:page')",ignoreDataScope = true)
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
     * 消息监控删除数据
	 *
     */
    @ApiOperation(value = "消息监控删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('message:monitor:delete')")
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
    @Permission("@ps.hasPerm('message:monitor:delete')")
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
