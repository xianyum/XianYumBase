package cn.xianyum.system.entity.response;

import lombok.Data;

import java.util.Date;

/**
 * 消息推送中心日志(push_log)
 *
 */
@Data
public class PushLogResponse {

    private String id;

    private String title;

    private String content;

    private Date createTime;


}
