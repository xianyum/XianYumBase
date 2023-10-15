package cn.xianyum.sheduler.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import cn.xianyum.sheduler.common.constant.ScheduleConstants;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
/**
 * @author zhangwei
 * @date 2022/5/12 21:44
 */
@Data
@TableName(value = "job")
public class JobEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long jobId;

    /** 任务名称 */
    private String jobName;

    /** jobHandler */
    private String jobHandler;

    /** cron执行表达式 */
    private String cronExpression;

    /** cron计划策略 */
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /** 是否并发执行（0允许 1禁止） */
    private String concurrent;

    /** 任务状态（0正常 1暂停） */
    private String status;

    /** 任务调用参数json串 */
    private String jobParams;

    /** 任务备注 */
    private String remark;

    /** 消息编码 */
    private String messageCode;
}
