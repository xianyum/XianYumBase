package cn.xianyum.sheduler.handler;

import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.SchedulerTool;
import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.xianyum.sheduler.common.constant.ScheduleConstants;
import cn.xianyum.sheduler.dao.JobLogMapper;
import cn.xianyum.sheduler.entity.po.JobEntity;
import cn.xianyum.sheduler.entity.po.JobLogEntity;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2022/5/12 21:42
 */
@Slf4j
public abstract class AbstractQuartzJob implements Job {

    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobEntity jobEntity = BeanUtils.copy(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES),JobEntity.class);
        SchedulerTool tool = new SchedulerTool(jobEntity.getJobId(), JSONObject.toJSONString(jobEntity));
        ReturnT returnT = null;
        String exceptionInfo = null;
        doBeforeJob(jobEntity,tool);
        try {
            Map<String,String> mapParam = JSONObject.parseObject(jobEntity.getJobParams(), Map.class);
            returnT = doExecute(jobEntity.getJobHandler(), mapParam, tool);
        }catch (Exception e){
            returnT = ReturnT.FAILURE;
            exceptionInfo = e.toString();
            executeSendMessage(jobEntity,e);
            log.error("",e);
        }finally {
            this.doFinishJob(jobEntity,returnT,tool,exceptionInfo);
        }

    }

    /**
     * 发送异常日志
     * @param jobEntity
     * @param e
     */
    private void executeSendMessage(JobEntity jobEntity, Exception e) {
        if(jobEntity != null && StringUtil.isNotEmpty(jobEntity.getMessageCode())){
            MessageSender bean = SpringUtils.getBean(MessageSender.class);
            Map<String,Object> content = new LinkedHashMap<>();
            content.put("任务名称：",jobEntity.getJobName());
            content.put("JobHandler：",jobEntity.getJobHandler());
            content.put("异常原因：",e.getMessage());
            MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
            messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
            bean.sendAsyncMessage(jobEntity.getMessageCode(),messageSenderEntity);
        }
    }

    /**
     * 执行前置事件
     * @param jobEntity
     * @param tool
     */
    private void doBeforeJob(JobEntity jobEntity, SchedulerTool tool) {
        threadLocal.set(new Date());
    }

    private void doFinishJob(JobEntity jobEntity,ReturnT returnT, SchedulerTool tool,String exceptionInfo) {
        tool.closeLogger();
        JobLogEntity jobLogEntity = new JobLogEntity();
        if (returnT == null) {
            returnT = ReturnT.FAILURE;
        }
        if (tool.getByteArrayOutputStream().size() > 0) {
            byte[] logBytes = tool.getByteArrayOutputStream().toByteArray();
            // 这个日志暂时没有想好该怎么处理。后期优化
            String logMessage = new String(logBytes, StandardCharsets.UTF_8);
        }

        Date startTime = threadLocal.get();
        Date endTime = new Date();
        jobLogEntity.setStartTime(startTime);
        jobLogEntity.setStopTime(endTime);
        threadLocal.remove();
        long runMs = endTime.getTime() - startTime.getTime();
        jobLogEntity.setJobRunTime(runMs);
        jobLogEntity.setExceptionInfo(exceptionInfo);
        Long status = null;
        switch (returnT) {
            case SUCCESS:
                // 成功
                status = ScheduleConstants.SUCCESS;
                break;// 失败
            default:
                // 失败其他原因
                status = ScheduleConstants.FAIL;
        }
        jobLogEntity.setStatus(status);
        jobLogEntity.setJobHandler(jobEntity.getJobHandler());
        jobLogEntity.setJobId(jobEntity.getJobId());
        jobLogEntity.setJobName(jobEntity.getJobName());
        SpringUtils.getBean(JobLogMapper.class).insert(jobLogEntity);
    }

    protected abstract ReturnT doExecute(String jobHandler, Map<String,String> mapParam, SchedulerTool tool) throws Exception;
}
