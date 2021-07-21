package cn.xianyum.analysis.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 消息推送中心日志(push_log)
 *
 */
@Data
@TableName(value = "push_log")
public class AnalysisPushLogEntity {

	@TableId(type = IdType.INPUT)
    private String id;

	/** 消息标题 */
    private String title;

    /** 消息摘要 */
    private String content;

    /** 推送时间 */
    private Date createTime;

}
