package cn.xianyum.message.entity.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangwei
 * @date 2021/11/21 17:21
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxTextCardMessage {

    //成员ID列表
    private String touser;
    //部门ID列表
    private String toparty;
    //标签ID列表
    private String totag;
    //消息类型
    private String msgtype;
    //	企业应用的id
    private String agentid;
    //表示是否开启id转译，0表示否，1表示是，默认0
    private Integer enable_id_trans;
    //表示是否开启重复消息检查，0表示否，1表示是，默认0
    private Integer enable_duplicate_check;
    //表示是否重复消息检查的时间间隔，默认1800s，最大不超过4小时
    private Integer duplicate_check_interval;

    //文本卡片类信息
    private TextCard textcard;

    public WxTextCardMessage(String agentId) {
        this.setMsgtype("textcard");
        this.setAgentid(agentId);
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TextCard{
        //标题，不超过128个字节，超过会自动截断（支持id转译）
        private String title;
        //描述，不超过512个字节，超过会自动截断（支持id转译）
        private String description;
        //点击后跳转的链接。最长2048字节，请确保包含了协议头(http/https)
        private String url;
        //按钮文字。 默认为“详情”， 不超过4个文字，超过自动截断。
        private String btntxt;
    }
}
