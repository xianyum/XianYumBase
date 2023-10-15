package cn.xianyum.sheduler.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.service.IJobHandler;
import cn.xianyum.common.utils.*;
import cn.xianyum.sheduler.common.constant.ScheduleConstants;
import cn.xianyum.sheduler.common.utils.CronUtils;
import cn.xianyum.sheduler.common.utils.ScheduleUtils;
import cn.xianyum.sheduler.dao.JobMapper;
import cn.xianyum.sheduler.entity.po.JobEntity;
import cn.xianyum.sheduler.entity.request.JobRequest;
import cn.xianyum.sheduler.entity.response.JobResponse;
import cn.xianyum.sheduler.service.JobService;
import com.alibaba.fastjson.JSONObject;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;

@Service
@Slf4j
public class JobServiceImpl implements JobService {

	@Autowired
	private JobMapper jobMapper;

	@Autowired
	private Scheduler scheduler;

	/**
	 * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
	 */
	@PostConstruct
	public void init() throws Exception {
		scheduler.clear();
		QueryWrapper<JobEntity> queryWrapper = new QueryWrapper<JobEntity>();
		List<JobEntity> jobList = jobMapper.selectList(queryWrapper);
		for (JobEntity job : jobList) {
			ScheduleUtils.createScheduleJob(scheduler, job);
		}
		log.info("init quartz job total size: {}",jobList.size());
	}

	@Override
	public IPage<JobResponse> getPage(JobRequest request) {

		Page<JobEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<JobEntity> queryWrapper = new QueryWrapper<JobEntity>()
				.like(StringUtil.isNotEmpty(request.getJobName()),"job_name",request.getJobName())
				.like(StringUtil.isNotEmpty(request.getJobHandler()),"job_handler",request.getJobHandler())
				.orderByDesc("create_time");
		IPage<JobEntity> pageResult = jobMapper.selectPage(page,queryWrapper);
		IPage<JobResponse> responseIPage = new Page<>();
		responseIPage.setTotal(pageResult.getTotal());
		responseIPage.setRecords(BeanUtils.copyList(pageResult.getRecords(),JobResponse.class));
		return responseIPage;
	}

	@Override
	public JobResponse getById(Long jobId) {

		if(null == jobId){
			throw new SoException("id不能为空");
		}
		JobEntity result = jobMapper.selectById(jobId);
		JobResponse response = BeanUtils.copy(result, JobResponse.class);
		if(response != null){
			response.setNextValidTime(CronUtils.getNextExecution(response.getCronExpression()));
		}
		return response;

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer save(JobRequest request) throws SchedulerException {
		if(StringUtil.isBlank(request.getJobName())){
			throw new SoException("定时任务名称不能为空！");
		}
		this.checkJobHandler(request.getJobHandler());
		this.checkJobParams(request.getJobParams());
		if (!CronUtils.isValid(request.getCronExpression())) {
			throw new SoException("新增任务'" + request.getJobName() + "'失败，Cron表达式不正确！");
		}
		JobEntity bean = BeanUtils.copy(request,JobEntity.class);
		bean.setStatus(ScheduleConstants.Status.PAUSE.getValue());
		int rows = jobMapper.insert(bean);
		if (rows > 0) {
			ScheduleUtils.createScheduleJob(scheduler, bean);
		}
		return rows;

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer update(JobRequest request) throws SchedulerException {

		if(null == request.getJobId()){
			throw new SoException("id不能为空");
		}
		if (!CronUtils.isValid(request.getCronExpression())) {
			throw new SoException("新增任务'" + request.getJobName() + "'失败，Cron表达式不正确！");
		}
		this.checkJobHandler(request.getJobHandler());
		this.checkJobParams(request.getJobParams());
		JobEntity bean = BeanUtils.copy(request,JobEntity.class);
		int rows = jobMapper.updateById(bean);
		if(rows > 0){
			updateSchedulerJob(bean.getJobId());
		}
		return rows;

	}

	public void updateSchedulerJob(Long jobId) throws SchedulerException{
		// 判断是否存在
		JobKey jobKey = ScheduleUtils.getJobKey(jobId);
		if (scheduler.checkExists(jobKey)) {
			// 防止创建时存在数据问题 先移除，然后在执行创建操作
			scheduler.deleteJob(jobKey);
		}
		JobEntity jobEntity = jobMapper.selectById(jobId);
		ScheduleUtils.createScheduleJob(scheduler, jobEntity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long[] ids) throws SchedulerException{

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (Long id : ids){
			int rows = jobMapper.deleteById(id);
			if (rows > 0) {
				scheduler.deleteJob(ScheduleUtils.getJobKey(id));
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int resumeJob(JobRequest job) throws SchedulerException{
		if(null == job.getJobId()){
			throw new SoException("jobId不能为空");
		}
		JobEntity jobEntity = new JobEntity();
		jobEntity.setJobId(job.getJobId());
		jobEntity.setStatus(ScheduleConstants.Status.NORMAL.getValue());
		int rows = jobMapper.updateById(jobEntity);
		if(rows > 0){
			scheduler.resumeJob(ScheduleUtils.getJobKey(job.getJobId()));
		}
		return rows;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int pauseJob(JobRequest job) throws SchedulerException{
		if(null == job.getJobId()){
			throw new SoException("jobId不能为空");
		}
		JobEntity jobEntity = new JobEntity();
		jobEntity.setJobId(job.getJobId());
		jobEntity.setStatus(ScheduleConstants.Status.PAUSE.getValue());
		int rows = jobMapper.updateById(jobEntity);
		if(rows > 0){
			scheduler.pauseJob(ScheduleUtils.getJobKey(job.getJobId()));
		}
		return rows;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int changeStatus(JobRequest request) throws SchedulerException{
		int rows = 0;
		String status = request.getStatus();
		if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
			rows = resumeJob(request);
		}
		else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
			rows = pauseJob(request);
		}
		return rows;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void runOnce(JobRequest request) throws SchedulerException{
		Long jobId = request.getJobId();
		if(null == jobId){
			throw new SoException("jobId不能为空");
		}
		JobEntity properties = jobMapper.selectById(jobId);
		if(Objects.isNull(properties)){
			throw new SoException("定时任务不存在");
		}
		// 参数
		JobDataMap dataMap = new JobDataMap();
		dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
		scheduler.triggerJob(ScheduleUtils.getJobKey(jobId), dataMap);
	}

	@Override
	public void checkJobHandler(String jobHandler) {
		if(StringUtil.isEmpty(jobHandler)){
			throw new SoException("jobHandler is null.");
		}
		Object iJobHandler = null;
		try {
			iJobHandler = SpringUtils.getBean(jobHandler);
		}catch (Exception e){
			throw new SoException("["+jobHandler+"]没有注册到spring容器中！"+e.getMessage());
		}

		if(Objects.isNull(iJobHandler)){
			throw new SoException("jobHandler invoke is null.");
		}
		if(!(iJobHandler instanceof IJobHandler)){
			throw new SoException("jobHandler not implements IJobHandler interface methods.");
		}
	}

	/**
	 * 校验jobParams是否为json类型
	 *
	 * @param jobParams
	 */
	@Override
	public void checkJobParams(String jobParams) {
		if(StringUtil.isNotEmpty(jobParams)){
			try {
				JSONObject.parseObject(jobParams);
			}catch (Exception e){
				throw new SoException("任务参数必须是JSON类型");
			}
		}
	}

}
