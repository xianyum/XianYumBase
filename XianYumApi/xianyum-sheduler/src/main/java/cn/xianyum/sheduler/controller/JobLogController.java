package cn.xianyum.sheduler.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.annotation.SysLog;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.sheduler.entity.request.JobLogRequest;
import cn.xianyum.sheduler.entity.response.JobLogResponse;
import cn.xianyum.sheduler.service.JobLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 定时任务调度日志表接口
 *
 */
@Api(tags = "定时任务调度日志表接口")
@RestController
@RequestMapping(value = "/xianyum-sheduler/v1/jobLog")
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
	public DataResult getPage(JobLogRequest request) {

		IPage<JobLogResponse> response = jobLogService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 定时任务调度日志表根据ID查询数据
     *
     */
    @ApiOperation(value = "定时任务调度日志表根据ID查询数据")
    @GetMapping(value = "/getById/{id}")
    public DataResult getById(@PathVariable Long id) {

        JobLogResponse response = jobLogService.getById(id);
        return DataResult.success(response);
    }

    /**
     * 清空日志
     *
     */
    @ApiOperation(value = "清空日志")
    @DeleteMapping(value = "/truncateLog")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    @SysLog(value = "清空调度任务日志")
    public DataResult truncateLog() {
        jobLogService.truncateLog();
        return DataResult.success();
    }

	/**
     * 定时任务调度日志表删除数据
	 *
     */
    @ApiOperation(value = "定时任务调度日志表删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    @SysLog(value = "批量删除调度任务日志")
    public DataResult delete(@RequestBody String[] ids) {

		jobLogService.deleteById(ids);
	    return DataResult.success();
    }


    /**
     * 获取任务调度数量
     *
     */
    @ApiOperation(value = "获取任务调度数量")
    @GetMapping(value = "/getJobLogCount")
    public DataResult getJobLogCount() {
        return DataResult.success(jobLogService.getJobLogCount());
    }
}
