package cn.xianyum.sheduler.controller;

import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.Results;
import cn.xianyum.sheduler.entity.request.JobRequest;
import cn.xianyum.sheduler.entity.response.JobResponse;
import cn.xianyum.sheduler.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 定时任务调度表接口
 *
 */
@Api(tags = "定时任务调度表接口")
@RestController
@RequestMapping(value = "/xianyum-sheduler/v1/job")
@Slf4j
public class JobController {

	@Autowired
	private JobService jobService;

    /**
     * 定时任务调度表分页查询数据
     *
     */
	@ApiOperation(value = "定时任务调度表分页查询数据")
	@GetMapping(value = "/getPage")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
	public Results getPage(JobRequest request) {

        PageResponse<JobResponse> response = jobService.getPage(request);
        return Results.page(response);
	}

    /**
     * 定时任务调度表根据ID查询数据
     *
     */
    @ApiOperation(value = "定时任务调度表根据ID查询数据")
    @GetMapping(value = "/getById/{jobId}")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results getById(@PathVariable Long jobId){
        JobResponse response = jobService.getById(jobId);
        return Results.success(response);
    }

    /**
     * 定时任务调度表保存数据
	 *
     */
    @ApiOperation(value = "定时任务调度表保存数据")
    @PostMapping(value = "/save")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results save(@RequestBody JobRequest request) {

        try {
            Integer count = jobService.save(request);
            if(count>0){
                return Results.success();
            }
        }catch (Exception e){
            return Results.error(e.getMessage());
        }
        return Results.error("保存失败");
    }

    /**
     * 定时任务调度表修改数据
	 *
     */
    @ApiOperation(value = "定时任务调度表修改数据")
    @PutMapping(value = "/update")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results update(@RequestBody JobRequest request) {

        try {
            Integer count = jobService.update(request);
            if(count>0){
                return Results.success();
            }
        }catch (Exception e){
            return Results.error(e.getMessage());
        }
        return Results.error("修改失败");
    }

	/**
     * 定时任务调度表删除数据
	 *
     */
    @ApiOperation(value = "定时任务调度表删除数据")
    @DeleteMapping(value = "/delete")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results delete(@RequestBody Long[] ids) {
        try {
            jobService.deleteById(ids);
            return Results.success();
        }catch (Exception e){
            return Results.error(e.getMessage());
        }
    }


    /**
     * 更新任务状态
     *
     */
    @ApiOperation(value = "更新任务状态")
    @PutMapping(value = "/changeStatus")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results changeStatus(@RequestBody JobRequest request) {
        try {
            jobService.changeStatus(request);
            return Results.success();
        }catch (Exception e){
            return Results.error(e.getMessage());
        }
    }

    /**
     * 立即执行一次
     * @param request
     * @return
     */
    @ApiOperation(value = "立即执行一次")
    @PutMapping(value = "/runOnce")
    @Permission(strategy = PermissionStrategy.ALLOW_ADMIN)
    public Results runOnce(@RequestBody JobRequest request) {
        try {
            jobService.runOnce(request);
            return Results.success();
        }catch (Exception e){
            return Results.error(e.getMessage());
        }
    }
}
