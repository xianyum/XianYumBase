package cn.xianyum.sheduler.service.impl;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.sheduler.dao.JobLogMapper;
import cn.xianyum.sheduler.entity.po.JobLogEntity;
import cn.xianyum.sheduler.entity.request.JobLogRequest;
import cn.xianyum.sheduler.entity.response.JobLogResponse;
import cn.xianyum.sheduler.service.JobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
@Slf4j
public class JobLogServiceImpl implements JobLogService {

	@Autowired
	private JobLogMapper jobLogMapper;

	@Override
	public PageResponse<JobLogResponse> getPage(JobLogRequest request) {

		Page<JobLogEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<JobLogEntity> queryWrapper = new QueryWrapper<JobLogEntity>()
				.eq(null != request.getStatus(),"status",request.getStatus())
				.eq(null != request.getJobId(),"job_id",request.getJobId())
				.like(StringUtil.isNotEmpty(request.getJobName()),"job_name",request.getJobName())
				.like(StringUtil.isNotEmpty(request.getJobHandler()),"job_handler",request.getJobHandler())
				.orderByDesc("start_time");;
		IPage<JobLogEntity> pageResult = jobLogMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,JobLogResponse.class);
	}

	@Override
	public JobLogResponse getById(Long id) {

		if(null == id){
			throw new SoException("id不能为空");
		}
		JobLogEntity result = jobLogMapper.selectById(id);
		JobLogResponse response = BeanUtils.copy(result, JobLogResponse.class);
		return response;

	}

	@Override
	public Integer save(JobLogRequest request) {

		JobLogEntity bean = BeanUtils.copy(request,JobLogEntity.class);
		return jobLogMapper.insert(bean);

	}

	@Override
	public Integer update(JobLogRequest request) {

		if(null == request.getId()){
			throw new SoException("id不能为空");
		}
		JobLogEntity bean = BeanUtils.copy(request,JobLogEntity.class);
		return jobLogMapper.updateById(bean);

	}

	@Override
	public void deleteById(String[] ids) {

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			jobLogMapper.deleteById(id);
		}
	}

	@Override
	public void truncateLog() {
		jobLogMapper.truncateLog();
	}

	@Override
	public Long getJobLogCount() {
		return jobLogMapper.selectCount(null);
	}

}
