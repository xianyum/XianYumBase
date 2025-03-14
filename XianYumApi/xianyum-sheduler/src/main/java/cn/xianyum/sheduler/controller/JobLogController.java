package cn.xianyum.sheduler.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.utils.Results;
import cn.xianyum.sheduler.entity.request.JobLogRequest;
import cn.xianyum.sheduler.entity.response.JobLogResponse;
import cn.xianyum.sheduler.service.JobLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务调度日志表接口
 *
 */
@Api(tags = "定时任务调度日志表接口")
@RestController
@RequestMapping(value = "xym-sheduler/v1/jobLog")
@Slf4j
public class JobLogController {

	@Autowired
	private JobLogService jobLogService;

    /**
     * 定时任务调度日志表分页查询数据
     *
     */
	@ApiOperation(value = "定时任务调度日志表分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission(value = "@ps.hasPerm('job:log:page')",ignoreDataScope = true)
	public Results getPage(JobLogRequest request) {
        PageResponse<JobLogResponse> response = jobLogService.getPage(request);
        return Results.page(response);
	}

    /**
     * 定时任务调度日志表根据ID查询数据
     *
     */
    @ApiOperation(value = "定时任务调度日志表根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    public Results getById(@PathVariable Long id) {

        JobLogResponse response = jobLogService.getById(id);
        return Results.success(response);
    }

    /**
     * 清空日志
     *
     */
    @ApiOperation(value = "清空调度任务日志")
    @DeleteMapping(value = "/truncateLog")
    @Permission("@ps.hasPerm('job:log:delete')")
    public Results truncateLog() {
        jobLogService.truncateLog();
        return Results.success();
    }

	/**
     * 定时任务调度日志表删除数据
	 *
     */
    @ApiOperation(value = "定时任务调度日志表删除数据")
    @DeleteMapping(value = "/delete")
    @Permission("@ps.hasPerm('job:log:delete')")
    public Results delete(@RequestBody String[] ids) {

		jobLogService.deleteById(ids);
	    return Results.success();
    }


    /**
     * 获取任务调度数量
     *
     */
    @ApiOperation(value = "获取任务调度数量")
    @GetMapping(value = "/getJobLogCount")
    public Results getJobLogCount() {
        return Results.success(jobLogService.getJobLogCount());
    }
}
