package cn.xianyum.message.infra.core;

import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.message.enums.MessageAccountTypeEnums;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 消息发送工厂类
 * @author zhangwei
 * @date 2025/4/19 21:11
 */
@Component
public class MessageFactory {

    private static final ConcurrentHashMap<String, AbstractMessageSender> ABSTRACT_MESSAGE_SERVICE_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;


    @PostConstruct
    public void init(){
        Map<String, AbstractMessageSender> beans = applicationContext.getBeansOfType(AbstractMessageSender.class);
        for (AbstractMessageSender service : beans.values()) {
            String messageAccountTypeCode = service.getMessageAccountTypeCode();
            if(StringUtil.isNotBlank(messageAccountTypeCode)){
                ABSTRACT_MESSAGE_SERVICE_CONCURRENT_HASH_MAP.put(messageAccountTypeCode,service);
            }
        }
    }

    /**
     * 获取发送消息服务
     * @param messageAccountTypeCode
     * @see MessageAccountTypeEnums
     * @return
     */
    public AbstractMessageSender getMessageService(String messageAccountTypeCode){
        return ABSTRACT_MESSAGE_SERVICE_CONCURRENT_HASH_MAP.get(messageAccountTypeCode);
    }

}
