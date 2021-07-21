package cn.xianyum.common.service;


/**
 * @author zhangwei
 * @date 2019/9/29 13:57
 */
public interface WxCenterService {

    void pushWxMessage(String pushTitle,String pushContent);

    String getWxToken();
}
