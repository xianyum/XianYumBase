package cn.xianyum.sheduler.service;

import cn.xianyum.sheduler.entity.request.JobLogRequest;
import cn.xianyum.sheduler.entity.response.JobLogResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface JobLogService {

	IPage<JobLogResponse> getPage(JobLogRequest request);

	JobLogResponse getById(Long id);

	Integer save(JobLogRequest request);

	Integer update(JobLogRequest request);

	void deleteById(String[] ids);

	/**
	 * 清空日志
	 * @param request
	 */
	void truncateLog();
}
