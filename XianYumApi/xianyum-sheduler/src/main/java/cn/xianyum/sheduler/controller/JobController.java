package cn.xianyum.sheduler.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.enums.PermissionStrategy;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.sheduler.entity.request.JobRequest;
import cn.xianyum.sheduler.entity.response.JobResponse;
import cn.xianyum.sheduler.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 定时任务调度表接口
 *
 */
@Api(tags = "定时任务调度表接口")
@RestController
@RequestMapping(value = "/v1/job")
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
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
	public DataResult getPage(JobRequest request) {

		IPage<JobResponse> response = jobService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 定时任务调度表根据ID查询数据
     *
     */
    @ApiOperation(value = "定时任务调度表根据ID查询数据")
    @GetMapping(value = "/getById/{jobId}")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult getById(@PathVariable Long jobId){
        JobResponse response = jobService.getById(jobId);
        return DataResult.success(response);
    }

    /**
     * 定时任务调度表保存数据
	 *
     */
    @ApiOperation(value = "定时任务调度表保存数据")
    @PostMapping(value = "/save")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult save(@RequestBody JobRequest request) {

        try {
            Integer count = jobService.save(request);
            if(count>0){
                return DataResult.success();
            }
        }catch (Exception e){
            return DataResult.error(e.getMessage());
        }
        return DataResult.error("保存失败");
    }

    /**
     * 定时任务调度表修改数据
	 *
     */
    @ApiOperation(value = "定时任务调度表修改数据")
    @PutMapping(value = "/update")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult update(@RequestBody JobRequest request) {

        try {
            Integer count = jobService.update(request);
            if(count>0){
                return DataResult.success();
            }
        }catch (Exception e){
            return DataResult.error(e.getMessage());
        }
        return DataResult.error("修改失败");
    }

	/**
     * 定时任务调度表删除数据
	 *
     */
    @ApiOperation(value = "定时任务调度表删除数据")
    @DeleteMapping(value = "/delete")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult delete(@RequestBody Long[] ids) {
        try {
            jobService.deleteById(ids);
            return DataResult.success();
        }catch (Exception e){
            return DataResult.error(e.getMessage());
        }
    }


    /**
     * 更新任务状态
     *
     */
    @ApiOperation(value = "更新任务状态")
    @PutMapping(value = "/changeStatus")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult changeStatus(@RequestBody JobRequest request) {
        try {
            jobService.changeStatus(request);
            return DataResult.success();
        }catch (Exception e){
            return DataResult.error(e.getMessage());
        }
    }

    /**
     * 立即执行一次
     * @param request
     * @return
     */
    @ApiOperation(value = "立即执行一次")
    @PutMapping(value = "/runOnce")
    @Permissions(strategy = PermissionStrategy.ALLOW_ADMIN)
    public DataResult runOnce(@RequestBody JobRequest request) {
        try {
            jobService.runOnce(request);
            return DataResult.success();
        }catch (Exception e){
            return DataResult.error(e.getMessage());
        }
    }
}
