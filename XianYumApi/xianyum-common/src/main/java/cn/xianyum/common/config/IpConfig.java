package cn.xianyum.common.config;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.FileUtils;
import cn.xianyum.common.utils.IpDbSearcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author zhangwei
 * @date 2020/4/1 14:14
 */
@Configuration
@Slf4j
public class IpConfig {

    @Value("${ip.db.path:xxxxxx}")
    private String dbFile;

    private final static String DWONLOAD_DB_URL = "https://gitee.com/lionsoul/ip2region/raw/master/v1.0/data/ip2region.db";
    private final static String DB_PATH = System.getProperty("user.dir")+"/ipdb/ip2region.db";

    @Bean(name = "ipDbSearcherConfig")
    public IpDbSearcher loadIpConfig(){
        try {
            File file = new File(dbFile);
            if(!file.exists()){
                log.info("本地未找到ip库(建议配置ip.db.path本地路径)，切换到从网络资源下载ip库，下载路径为：{}",dbFile);
                FileUtils.copyByUrl(DWONLOAD_DB_URL,dbFile);
            }
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
