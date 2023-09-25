package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.request.MessageMonitorRequest;
import cn.xianyum.message.entity.response.MessageMonitorResponse;
import cn.xianyum.message.service.MessageMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 消息监控接口
 *
 */
@Api(tags = "消息监控接口")
@RestController
@RequestMapping(value = "/v1/messageMonitor")
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
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getPage(MessageMonitorRequest request) {

		IPage<MessageMonitorResponse> response = messageMonitorService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 消息监控根据ID查询数据
     *
     */
    @ApiOperation(value = "消息监控根据ID查询数据")
    @GetMapping(value = "/getById")
    public DataResult getById(@RequestParam String id) {
        MessageMonitorResponse response = messageMonitorService.getById(id);
        return DataResult.success(response);
    }

    /**
     * 消息监控保存数据
	 *
     */
    @ApiOperation(value = "消息监控保存数据")
    @PostMapping(value = "/save")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult save(@RequestBody MessageMonitorRequest request) {

		Integer count = messageMonitorService.save(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("保存失败");
    }

    /**
     * 消息监控修改数据
	 *
     */
    @ApiOperation(value = "消息监控修改数据")
    @PutMapping(value = "/update")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult update(@RequestBody MessageMonitorRequest request) {

		Integer count = messageMonitorService.update(request);
		if(count>0){
			return DataResult.success();
		}
		return DataResult.error("修改失败");
    }

	/**
     * 消息监控删除数据
	 *
     */
    @ApiOperation(value = "消息监控删除数据")
    @DeleteMapping(value = "/delete")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult delete(@RequestBody String[] ids) {
		messageMonitorService.deleteById(ids);
	    return DataResult.success();
    }


    /**
     * 清空监控删除数据
     *
     */
    @ApiOperation(value = "清空监控删除数据")
    @DeleteMapping(value = "/truncate")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult truncate() {
        messageMonitorService.truncate();
        return DataResult.success();
    }


    /**
     * 获取消息发送数量
     *
     */
    @ApiOperation(value = "获取消息发送数量")
    @GetMapping(value = "/getMessageLogCount")
    public DataResult getMessageLogCount() {
        return DataResult.success(messageMonitorService.getOnlineProxyCount());
    }
}
