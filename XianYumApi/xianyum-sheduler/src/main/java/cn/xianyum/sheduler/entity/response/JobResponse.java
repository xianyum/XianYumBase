package cn.xianyum.sheduler.entity.response;

import lombok.Data;
import java.util.Date;

/**
 * 定时任务调度表(job)
 *
 */
@Data
public class JobResponse{

    private Long jobId;

    private String jobName;

    private String jobHandler;

    private String cronExpression;

    private String misfirePolicy;

    private String concurrent;

    private String status;

    private String createUserId;

    private Date createTime;

    private String createUserName;

    private String remark;

    private String messageCode;

    private String jobParams;

    private Date nextValidTime;
}
