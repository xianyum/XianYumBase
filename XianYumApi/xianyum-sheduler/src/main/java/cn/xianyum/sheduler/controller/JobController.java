package cn.xianyum.sheduler.controller;

import cn.xianyum.common.annotation.Permissions;
import cn.xianyum.common.utils.DataResult;
import cn.xianyum.sheduler.entity.request.JobRequest;
import cn.xianyum.sheduler.entity.response.JobResponse;
import cn.xianyum.sheduler.service.JobService;
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
	@PostMapping(value = "/getPage")
    @Permissions(value = {"visitor","common"})
	public DataResult getPage(@RequestBody JobRequest request) {

		IPage<JobResponse> response = jobService.getPage(request);
        return DataResult.success(response);
	}

    /**
     * 定时任务调度表根据ID查询数据
     *
     */
    @ApiOperation(value = "定时任务调度表根据ID查询数据")
    @PostMapping(value = "/getById")
    @Permissions(value = {"visitor","common"})
    public DataResult getById(@RequestBody JobRequest request) {
        JobResponse response = jobService.getById(request);
        return DataResult.success(response);
    }

    /**
     * 定时任务调度表保存数据
	 *
     */
    @ApiOperation(value = "定时任务调度表保存数据")
    @PostMapping(value = "/save")
    @Permissions(value = {"visitor","common"})
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
    @PostMapping(value = "/update")
    @Permissions(value = {"visitor","common"})
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
    @PostMapping(value = "/delete")
    @Permissions(value = {"visitor","common"})
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
    @PostMapping(value = "/changeStatus")
    @Permissions(value = {"visitor","common"})
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
    @PostMapping(value = "/runOnce")
    @Permissions(value = {"visitor","common"})
    public DataResult runOnce(@RequestBody JobRequest request) {
        try {
            jobService.runOnce(request);
            return DataResult.success();
        }catch (Exception e){
            return DataResult.error(e.getMessage());
        }
    }
}
