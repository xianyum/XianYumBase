package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;

import java.util.Date;

/**
 * 消息推送中心日志(push_log)
 *
 */
@Data
public class PushLogRequest extends BaseRequest {


    private String id;

    private String title;

    private String content;

    private Date startTime;

    private Date endTime;
}
