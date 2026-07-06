package cn.xianyum.message.entity.request;

import lombok.Data;
import java.util.Date;
import java.util.List;

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
    private GrafanaCommonAnnotations commonAnnotations;

    /**
     * 消息体
     */
    private List<GrafanaAlerts> alerts;

    @Data
    public static class GrafanaCommonAnnotations  {
        /**
         * 标题
         */
        private String summary;

        /**
         * 内容
         */
        private String description;
    }

    @Data
    public static class GrafanaAlerts  {
        /** 告警开始时间 */
        private Date startsAt;
        /** 告警结束时间 */
        private Date endsAt;
        /** Grafana告警规则详情页链接 */
        private String generatorURL;
        /** 告警唯一指纹标识，同一条告警指纹固定，用于区分不同告警 */
        private String fingerprint;
        /** 告警静音页面链接，可临时屏蔽该告警通知 */
        private String silenceURL;
        /** 关联仪表盘地址，无仪表盘则为空字符串 */
        private String dashboardURL;
        /** 告警对应的图表面板地址，未绑定面板则为空 */
        private String panelURL;
        /** Grafana告警规则唯一UID */
        private String ruleUID;
        /** 告警阈值指标原始数值集合，为空时为null */
        private Object values;
        /** 告警触发时的指标数值文本描述，展示查询值、阈值等信息 */
        private String valueString;
        /** Grafana组织ID，多租户区分不同组织 */
        private Integer orgId;
    }


}
