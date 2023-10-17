package cn.xianyum.sheduler.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;
import java.util.Date;

/**
 * 定时任务调度表(job)
 *
 */
@Data
public class JobResponse extends BaseResponse {

    private Long jobId;

    private String jobName;

    private String jobHandler;

    private String cronExpression;

    private String misfirePolicy;

    private String concurrent;

    private String status;

    private String remark;

    private String messageCode;

    private String jobParams;

    private Date nextValidTime;
}
