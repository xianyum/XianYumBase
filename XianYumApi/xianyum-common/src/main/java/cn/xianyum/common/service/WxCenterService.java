package cn.xianyum.common.service;


import java.util.Map;

/**
 * @author zhangwei
 * @date 2019/9/29 13:57
 */
public interface WxCenterService {

    void pushWxMessage(String pushTitle, Map<String, Object> pushContent);

    String getWxToken();
}
