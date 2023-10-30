package cn.xianyum.system.service.impl;

import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.xianyum.system.service.AliNetService;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2019/11/9 17:07
 * @desc
 */
@Service
@Slf4j
public class AliNetServiceImpl implements AliNetService {

    @Autowired
    private MessageSender messageSender;

    @Value("${ali.login.app_private_key}")
    private String APP_PRIVATE_KEY;

    @Value("${ali.login.alipay_public_key}")
    private String ALIPAY_PUBLIC_KEY;

    @Value("${ali.login.app_id}")
    private String APP_ID;

    @Value("${ali.login.server_url}")
    private String SERVER_URL;

    private static final String FORMAT="json";
    private static final String CHAR_SET="utf-8";
    private static final String SIGN_TYPE="RSA2";


    @Override
    public String getAccessToken(String authCode) {
        AlipayClient alipayClient = new DefaultAlipayClient(SERVER_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHAR_SET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(authCode);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            return oauthTokenResponse.getAccessToken();
        } catch (AlipayApiException e) {
            //处理异常
            log.error("获取支付宝用户accestoken失败，{}",e.getErrMsg());
        }
        return null;
    }

    @Override
    public AlipayUserInfoShareResponse getALiUserInfo(String accessToken) {
        AlipayClient alipayClient = new DefaultAlipayClient(SERVER_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHAR_SET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        try {
            AlipayUserInfoShareResponse response = alipayClient.execute(request, accessToken);
            log.info("第三方支付宝登录,{}", JSONObject.toJSONString(response));
            return response;
        } catch (AlipayApiException e) {
            //处理异常
            log.error("获取支付宝用户详细信息失败，{}",e.getErrMsg());
        }
        return null;
    }

    @Override
    public void yunXiaoFlowCallBack(String requestInfo) {
        JSONObject callBackEventObj = JSONObject.parseObject(requestInfo);
        JSONObject taskObj = JSONObject.parseObject(callBackEventObj.getString("task"));
        // 流水线名称
        String pipelineName = taskObj.getString("pipelineName");
        // 任务名称
        String taskName = taskObj.getString("taskName");
        // 流水线运行状态
        String statusName = taskObj.getString("statusName");
        // 流水线url
        String pipelineUrl = taskObj.getString("pipelineUrl");

        Map<String,Object> content = new LinkedHashMap<>();
        content.put("流水线名称：",pipelineName);
        content.put("任务名称：",taskName);
        content.put("运行状态：",statusName);
        content.put("流水线详情：",pipelineUrl);
        MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
        messageSenderEntity.setFormUrl(pipelineUrl);
        messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
        messageSender.sendAsyncMessage(MessageCodeEnums.ALI_YUN_XIAO_NOTIFY.getMessageCode(),messageSenderEntity);
    }
}
