package cn.xianyum.system.controller;

import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.entity.request.PushLogRequest;
import cn.xianyum.system.entity.response.PushLogResponse;
import cn.xianyum.system.service.PushLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息推送中心日志接口
 *
 */
@Api(tags = "消息推送中心日志接口")
@RestController
@RequestMapping(value = "/pushLog")
@Slf4j
public class PushLogController {

	@Autowired
	private PushLogService pushLogService;

    /**
     * 消息推送中心日志分页查询数据
     *
     */
	@ApiOperation(value = "消息推送中心日志分页查询数据")
	@PostMapping(value = "/getPage")
	public DataResult getPage(@RequestBody PushLogRequest request) {

		IPage<PushLogResponse> response = pushLogService.getPage(request);
        return DataResult.success(response);
	}

}
