package cn.xianyum.message.service.impl;

import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.SystemConstantUtils;
import cn.xianyum.message.entity.po.MessageContent;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.entity.request.FnOsPushMessageRequest;
import cn.xianyum.message.entity.request.GrafanaAlertWebhookRequest;
import cn.xianyum.message.entity.request.MessageSenderRequest;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author wei.zhang@onecontract-cloud.com
 * @description
 * @date 2022/1/3 18:58
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageSender messageSender;

    private static final String MESSAGE_CONTENT_PREFIX = "";
    /**
     * 发送简单消息
     *
     * @param messageCode
     * @param title
     * @param content
     */
    @Override
    public void sendSimpleMessage(String messageCode, String title, String content) {
        MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
        messageSenderEntity.setTitle(title);
        List<MessageContent> messageContents = new ArrayList<>();
        MessageContent messageContent = new MessageContent();
        messageContent.setLabel(MESSAGE_CONTENT_PREFIX);
        messageContent.setValue(content);
        messageContents.add(messageContent);
        messageSenderEntity.setMessageContents(messageContents);
        messageSender.sendAsyncMessage(messageCode,messageSenderEntity);
    }

    /**
     * 发送标准消息
     * @param messageSenderRequest
     */
    @Override
    public void sendStandardMessage(MessageSenderRequest messageSenderRequest) {
        MessageSenderEntity messageSenderEntity = BeanUtils.copy(messageSenderRequest, MessageSenderEntity.class);
        List<MessageContent> messageContents = new ArrayList<>();
        MessageContent messageContent = new MessageContent();
        messageContent.setLabel(MESSAGE_CONTENT_PREFIX);
        messageContent.setValue(messageSenderRequest.getContent());
        messageContents.add(messageContent);
        messageSenderEntity.setMessageContents(messageContents);
        messageSender.sendAsyncMessage(messageSenderRequest.getMessageCode(),messageSenderEntity);
    }

    /**
     * 接收飞牛推送的消息
     *
     * @param request
     */
    @Override
    public void receiveFnOsPushMessage(FnOsPushMessageRequest request) {
        MessageSenderEntity messageSenderEntity =new MessageSenderEntity();
        List<MessageContent> messageContents = new ArrayList<>();
        if(StringUtil.isBlank(request.getContent())){
            return;
        }
        List<String> contentList = Arrays.asList(request.getContent().split("\n\n"));
        for (String content : contentList) {
            MessageContent messageContent = new MessageContent();
            messageContent.setLabel(MESSAGE_CONTENT_PREFIX);
            messageContent.setValue(content);
            messageContents.add(messageContent);
        }
        messageSenderEntity.setTitle(request.getTitle());
        messageSenderEntity.setMessageContents(messageContents);
        messageSender.sendAsyncMessage(SystemConstantUtils.getValueByKey(SystemConstantKeyEnum.FN_OS_MESSAGE_CODE),messageSenderEntity);
    }

    /**
     * 接受GrafanaAlert发送消息
     *
     * @param request
     */
    @Override
    public void receiveGrafanaAlert(GrafanaAlertWebhookRequest request) {
        String status = request.getStatus();
        String statusEmoji;
        String statusText;
        if ("firing".equals(status)) {
            statusEmoji = "🚨";
            statusText = "告警";
        } else{
            statusEmoji = "✅";
            statusText = "已恢复";
        }
        String summary = request.getCommonAnnotations().getSummary();
        String title = String.format("%s%s-%s", statusEmoji,summary,statusText);
        String content = request.getCommonAnnotations().getDescription();
        List<MessageContent> messageContents = new ArrayList<>();
        MessageContent messageContent = new MessageContent();
        messageContent.setLabel(MESSAGE_CONTENT_PREFIX);
        messageContent.setValue(content);
        messageContents.add(messageContent);

        // 判断恢复时间
        List<GrafanaAlertWebhookRequest.GrafanaAlerts> alerts = request.getAlerts();
        if(!CollectionUtils.isEmpty(alerts) && Objects.nonNull(alerts.get(0).getEndsAt()) && Objects.nonNull(alerts.get(0).getStartsAt())){
            long diffSecond = (alerts.get(0).getEndsAt().getTime() - alerts.get(0).getStartsAt().getTime()) / 1000;
            if(diffSecond > 0){
                MessageContent diffSecondContent = new MessageContent();
                diffSecondContent.setLabel("持续时间：");
                diffSecondContent.setValue(DateUtils.getDatePoor(diffSecond));
                messageContents.add(diffSecondContent);
            }
        }
        MessageSenderEntity messageSenderEntity =new MessageSenderEntity();
        messageSenderEntity.setTitle(title);
        messageSenderEntity.setMessageContents(messageContents);
        messageSender.sendAsyncMessage(SystemConstantUtils.getValueByKey(SystemConstantKeyEnum.GRAFANA_MESSAGE_CODE),messageSenderEntity);
    }

}
