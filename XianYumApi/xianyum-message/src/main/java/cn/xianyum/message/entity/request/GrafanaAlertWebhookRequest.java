package cn.xianyum.message.entity.request;

import lombok.Data;

/**
 * @author xianyum
 * @date 2026/6/5 19:47
 */
@Data
public class GrafanaAlertWebhookRequest {

    /**
     * status.equals("firing") ? "告警中" : "已恢复";
     */
    private String status;

    /**
     * 消息体
     */
    private commonAnnotations commonAnnotations;

    @Data
    public static class commonAnnotations  {
        /**
         * 标题
         */
        private String summary;

        /**
         * 内容
         */
        private String description;
    }


}
