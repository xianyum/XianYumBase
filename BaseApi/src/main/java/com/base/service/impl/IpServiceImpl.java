package com.base.service.impl;

import com.base.common.exception.SoException;
import com.base.common.utils.IpDataBlock;
import com.base.common.utils.IpDbSearcher;
import com.base.common.utils.StringUtil;
import com.base.config.IpDbConfig;
import com.base.entity.po.IpInfoEntity;
import com.base.service.iservice.IpService;
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
            throw new SoException("ip不能为空");
        }
        try {
            IpDataBlock dataBlock = ipDbSearcher.btreeSearch(ip);
            String[] data = dataBlock.getRegion().split("\\|");
            IpInfoEntity ipInfoEntity = new IpInfoEntity();
            ipInfoEntity.setCountry("0".equals(data[0])?"":data[0]);
            ipInfoEntity.setProv("0".equals(data[2])?"":data[2]);
            ipInfoEntity.setCity("0".equals(data[3])?"":data[3]);
            ipInfoEntity.setIsp("0".equals(data[4])?"":data[4]);
            return ipInfoEntity;
        }catch (Exception e){
            return new IpInfoEntity();
        }
    }
}
