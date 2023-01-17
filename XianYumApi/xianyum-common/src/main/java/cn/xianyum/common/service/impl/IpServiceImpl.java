package cn.xianyum.common.service.impl;

import cn.xianyum.common.entity.IpInfoEntity;
import cn.xianyum.common.service.IpService;
import cn.xianyum.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author zhangwei
 * @date 2020/4/1 13:46
 */
@Service
@Slf4j
public class IpServiceImpl implements IpService {

    @Resource(name = "ipDbSearcherConfig")
    private IpDbSearcher ipDbSearcher;

    @Override
    public IpInfoEntity getIpInfo(String ip) {
        if(StringUtil.isBlank(ip)){
            ip = IPUtils.getIpAddr(HttpContextUtils.getHttpServletRequest());
        }
        try {
            IpDataBlock dataBlock = ipDbSearcher.btreeSearch(ip);
            String[] data = dataBlock.getRegion().split("\\|");
            IpInfoEntity ipInfoEntity = new IpInfoEntity();
            ipInfoEntity.setCountry("0".equals(data[0])?"":data[0]);
            ipInfoEntity.setProv("0".equals(data[2])?"":data[2]);
            ipInfoEntity.setCity("0".equals(data[3])?"":data[3]);
            ipInfoEntity.setIsp("0".equals(data[4])?"":data[4]);
            ipInfoEntity.setSupport("https://xiaoyaxiaokeai.gitee.io/base/20201224/thanks.jpg");
            ipInfoEntity.setEmail("80616059@qq.com");
            ipInfoEntity.setRemark("2023年新年快乐~");
            ipInfoEntity.setIp(ip);
            return ipInfoEntity;
        }catch (Exception e){
            return new IpInfoEntity();
        }
    }
}
