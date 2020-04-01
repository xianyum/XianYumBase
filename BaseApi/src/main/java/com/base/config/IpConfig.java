package com.base.config;

import com.base.common.exception.SoException;
import com.base.common.utils.IpDbSearcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangwei
 * @date 2020/4/1 14:14
 */
@Configuration
@Slf4j
public class IpConfig {

    @Value("${ip.db.path}")
    private String dbFile;

    @Bean(name = "ipDbSearcherConfig")
    public IpDbSearcher loadIpConfig(){
        try {
            IpDbConfig config = new IpDbConfig();
            IpDbSearcher searcher = new IpDbSearcher(config, dbFile);
            log.info("ip config load success.");
            return searcher;
        }catch (Exception e){
            log.error("ip config load error：{}",e.getMessage());
            throw new SoException("ip config load error："+e.getMessage());
        }
    }
}
