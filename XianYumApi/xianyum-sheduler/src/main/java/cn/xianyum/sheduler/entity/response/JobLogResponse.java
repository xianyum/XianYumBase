package cn.xianyum.sheduler.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;
import java.util.Date;

/**
 * 定时任务调度日志表(job_log)
 *
 */
@Data
public class JobLogResponse extends BaseResponse {

    private Long id;

    private Long jobId;

    private String jobName;

    private String jobHandler;

    private Long status;

    private String exceptionInfo;

    private Date startTime;

    private Date stopTime;

    private Long jobRunTime;

    /** 执行ip */
    private String executeIp;

}
