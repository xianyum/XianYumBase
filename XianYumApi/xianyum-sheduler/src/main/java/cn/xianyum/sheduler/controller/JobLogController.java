package cn.xianyum.sheduler.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.sheduler.entity.request.JobLogRequest;
import cn.xianyum.sheduler.entity.response.JobLogResponse;
import cn.xianyum.sheduler.service.JobLogService;
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
 * 定时任务调度日志表接口
 *
 */
@Api(tags = "定时任务调度日志表接口")
@RestController
@RequestMapping(value = "/v1/jobLog")
@Slf4j
public class JobLogController {

	@Autowired
	private JobLogService jobLogService;

    /**
     * 定时任务调度日志表分页查询数据
     *
     */
	@ApiOperation(value = "定时任务调度日志表分页查询数据")
	@PostMapping(value = "/getPage")
	public DataResult getPage(@RequestBody JobLogRequest request) {

		IPage<JobLogResponse> response = jobLogService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 定时任务调度日志表根据ID查询数据
     *
     */
    @ApiOperation(value = "定时任务调度日志表根据ID查询数据")
    @PostMapping(value = "/getById")
    public DataResult getById(@RequestBody JobLogRequest request) {

        JobLogResponse response = jobLogService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 清空日志
     *
     */
    @ApiOperation(value = "清空日志")
    @PostMapping(value = "/truncateLog")
    @Permissions(value = {"visitor","common"})
    public DataResult truncateLog(@RequestBody JobLogRequest request) {

        jobLogService.truncateLog(request);
        return DataResult.success();
    }

	/**
     * 定时任务调度日志表删除数据
	 *
     */
    @ApiOperation(value = "定时任务调度日志表删除数据")
    @PostMapping(value = "/delete")
    @Permissions(value = {"visitor","common"})
    public DataResult delete(@RequestBody String[] ids) {

		jobLogService.deleteById(ids);
	    return DataResult.success();
    }

}
