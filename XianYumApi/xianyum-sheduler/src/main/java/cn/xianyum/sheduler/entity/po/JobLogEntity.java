package cn.xianyum.sheduler.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author wei.zhang@onecontract-cloud.com
 * @description
 * @date 2022/5/13 14:21
 */
@Data
@TableName(value = "job_log")
public class JobLogEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 任务id */
    private Long jobId;

    /** 任务名称 */
    private String jobName;

    /** 任务handler */
    private String jobHandler;

    /** job执行时间ms */
    private Long jobRunTime;

    /** 执行状态（0正常 1失败） */
    private Long status;

    /** 异常信息 */
    private String exceptionInfo;

    /** 开始时间 */
    private Date startTime;

    /** 停止时间 */
    private Date stopTime;

    /** 执行ip */
    private String executeIp;
}

