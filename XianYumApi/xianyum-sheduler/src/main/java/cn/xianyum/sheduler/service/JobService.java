package cn.xianyum.sheduler.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.sheduler.entity.request.JobRequest;
import cn.xianyum.sheduler.entity.response.JobResponse;
import org.quartz.SchedulerException;

public interface JobService {

	PageResponse<JobResponse> getPage(JobRequest request);

	JobResponse getById(Long jobId);

	Integer save(JobRequest request) throws SchedulerException;

	Integer update(JobRequest request) throws SchedulerException;

	void deleteById(Long[] ids) throws SchedulerException;

	/**
	 * 恢复任务
	 * @param job
	 * @return
	 */
	int resumeJob(JobRequest job) throws SchedulerException;

	/**
	 * 暂停任务
	 * @param job
	 * @return
	 */
	int pauseJob(JobRequest job) throws SchedulerException;

	int changeStatus(JobRequest request) throws SchedulerException;

	/**
	 * 立即执行一次
	 * @param request
	 */
	void runOnce(JobRequest request) throws SchedulerException;

	/**
	 * 校验jobHandler
	 * @param jobHandler
	 */
	void checkJobHandler(String jobHandler);

	/**
	 * 校验jobParams是否为json类型
	 * @param jobParams
	 */
	void checkJobParams(String jobParams);
}
