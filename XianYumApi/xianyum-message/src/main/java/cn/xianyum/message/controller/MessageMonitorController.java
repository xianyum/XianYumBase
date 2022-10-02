package cn.xianyum.message.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.message.entity.request.MessageMonitorRequest;
import cn.xianyum.message.entity.response.MessageMonitorResponse;
import cn.xianyum.message.service.MessageMonitorService;
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
	@PostMapping(value = "/getPage")
    @Permissions(value = {"visitor","common"})
	public DataResult getPage(@RequestBody MessageMonitorRequest request) {

		IPage<MessageMonitorResponse> response = messageMonitorService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 消息监控根据ID查询数据
     *
     */
    @ApiOperation(value = "消息监控根据ID查询数据")
    @PostMapping(value = "/getById")
    public DataResult getById(@RequestBody MessageMonitorRequest request) {
        MessageMonitorResponse response = messageMonitorService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 消息监控保存数据
	 *
     */
    @ApiOperation(value = "消息监控保存数据")
    @PostMapping(value = "/save")
    @Permissions(value = {"visitor","common"})
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
    @PostMapping(value = "/update")
    @Permissions(value = {"visitor","common"})
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
    @PostMapping(value = "/delete")
    @Permissions(value = {"visitor","common"})
    public DataResult delete(@RequestBody String[] ids) {

		messageMonitorService.deleteById(ids);
	    return DataResult.success();
    }

}
