package cn.xianyum.message.entity.request;

import lombok.Data;

/**
 * @author xianyum
 * @date 2026/5/24 22:41
 */
@Data
public class FnOsPushMessageRequest {

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;
}
