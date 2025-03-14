package cn.xianyum.extension.service.impl;

import cn.xianyum.common.utils.*;
import cn.xianyum.extension.entity.po.IpInfoEntity;
import cn.xianyum.extension.service.IpService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author zhangwei
 * @date 2020/4/1 13:46
 */
@Service
@Slf4j
public class IpServiceImpl implements IpService {


    @Override
    public IpInfoEntity getIpInfo(String ip) {
        if(StringUtil.isBlank(ip)){
            ip = IPUtils.getIpAddr(HttpContextUtils.getHttpServletRequest());
        }
        IpInfoEntity ipInfoEntity = JSONObject.parseObject(JSONObject.toJSONString(IPUtils.getIpInfoMap(ip)),IpInfoEntity.class);
        ipInfoEntity.setSupport("https://xiaoyaxiaokeai.gitee.io/base/20201224/thanks.jpg");
        ipInfoEntity.setEmail("80616059@qq.com");
        ipInfoEntity.setIp(ip);
        return ipInfoEntity;
    }
}
