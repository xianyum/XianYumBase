package cn.xianyum.sheduler.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;
import java.util.Date;

/**
 * 定时任务调度日志表(job_log)
 *
 */
@Data
public class JobLogRequest extends BaseRequest {


    private Long id;

    private Long jobId;

    private String jobName;

    private String jobHandler;

    private Long status;

    private String exceptionInfo;

    private Date startTime;

    private Date stopTime;

    private Integer jobRunTime;


}
