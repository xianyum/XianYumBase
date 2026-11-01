package cn.xianyum.sheduler.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.xianyum.common.annotation.OperLog;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.OperTypeEnum;
import cn.xianyum.common.utils.Results;
import cn.xianyum.sheduler.entity.request.JobRequest;
import cn.xianyum.sheduler.entity.response.JobResponse;
import cn.xianyum.sheduler.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 定时任务调度表接口
 *
 */
@Tag(name = "定时任务调度表接口")
@RestController
@RequestMapping(value = "xym-sheduler/v1/job")
@Slf4j
@OperLog(module = "定时任务")
public class JobController {

	@Autowired
	private JobService jobService;

    /**
     * 定时任务调度表分页查询数据
     *
     */
	@Operation(summary = "定时任务调度表分页查询数据")
	@GetMapping(value = "/getPage")
    @SaCheckPermission("job:mange:page")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "分页查询定时任务")
	public Results getPage(JobRequest request) {
        PageResponse<JobResponse> response = jobService.getPage(request);
        return Results.page(response);
	}

    /**
     * 定时任务调度表根据ID查询数据
     *
     */
    @Operation(summary = "定时任务调度表根据ID查询数据")
    @GetMapping(value = "/getById/{jobId}")
    @SaCheckPermission("job:mange:query")
    @OperLog(operType = OperTypeEnum.QUERY, desc = "根据ID查询定时任务")
    public Results getById(@PathVariable Long jobId){
        JobResponse response = jobService.getById(jobId);
        return Results.success(response);
    }

    /**
     * 定时任务调度表保存数据
	 *
     */
    @Operation(summary = "定时任务调度表保存数据")
    @PostMapping(value = "/save")
    @SaCheckPermission("job:mange:save")
    @OperLog(operType = OperTypeEnum.ADD, desc = "新增定时任务")
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
    @Operation(summary = "定时任务调度表修改数据")
    @PutMapping(value = "/update")
    @SaCheckPermission("job:mange:update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "修改定时任务")
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
    @Operation(summary = "定时任务调度表删除数据")
    @DeleteMapping(value = "/delete")
    @SaCheckPermission("job:mange:delete")
    @OperLog(operType = OperTypeEnum.DELETE, desc = "删除定时任务")
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
    @Operation(summary = "更新任务状态")
    @PutMapping(value = "/changeStatus")
    @SaCheckPermission("job:mange:update")
    @OperLog(operType = OperTypeEnum.EDIT, desc = "更新任务状态")
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
    @Operation(summary = "立即执行一次")
    @PutMapping(value = "/runOnce")
    @SaCheckPermission("job:mange:once")
    @OperLog(operType = OperTypeEnum.OTHER, desc = "立即执行定时任务")
    public Results runOnce(@RequestBody JobRequest request) {
        try {
            jobService.runOnce(request);
            return Results.success();
        }catch (Exception e){
            return Results.error(e.getMessage());
        }
    }
}
