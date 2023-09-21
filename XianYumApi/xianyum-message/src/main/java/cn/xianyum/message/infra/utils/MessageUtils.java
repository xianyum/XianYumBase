package cn.xianyum.message.infra.utils;

import cn.xianyum.common.config.XianYumConfig;
import cn.xianyum.common.utils.SpringUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.message.entity.po.MessageContent;
import java.util.*;

/**
 * 消息工具类
 * @author wei.zhang
 * @description
 * @date 2021/11/26 15:40
 */
public class MessageUtils {

    /**
     * map转为消息体内容
     * @param content
     * @return
     */
    public static List<MessageContent> mapConvertMessageContentEntity(Map<String,Object> content){
        List<MessageContent> messageContents = new ArrayList<>();
        if(content != null && content.size() > 0){
            for (Map.Entry<String, Object> entry : content.entrySet()) {
                MessageContent messageContent = new MessageContent();
                // 防止map中value为空，导致空指针异常
                if(StringUtil.isNotEmpty(entry.getKey()) && null != entry.getValue()){
                    messageContent.setLabel(entry.getKey());
                    messageContent.setValue(entry.getValue().toString());
                    messageContents.add(messageContent);
                }
            }
        }
        return messageContents;
    }


    /**
     * 获取跳转详情页面地址
     * @param id
     * @return
     */
    public static String getFormUrl(String id){
        String webUrl = XianYumConfig.getXianYumConfig().getWebUrl();
        return webUrl+"/messageDetail?messageId="+id;
    }
}
