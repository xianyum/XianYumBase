package cn.xianyum.common.config;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.ip.IpSearcher;
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
    private String dbFilePath;

//    private final static String DOWNLOAD_DB_URL = "https://developer-oss.lanzouc.com/file/?AmQFOw4/U2IEDVFpBzIFaQA/VGxfaQJwCz4Dd1xlA2VSO1RsW2hXLQclAixTc1ZxAz8OYgAzUzRXD1Q7XWUBPQIzBWEOZlM/BGZRMwdgBTAAbVR3X2YCcwsxAzRcMQMyUmVUM1s3VzAHYQJyU3dWJwNkDjkAb1NjV2NUfV0xATkCLQViDmpTKARlUWUHZwVnAD5UMF80AjMLOgMyXDcDMVJhVGZbY1cyBzMCYlNmVjIDYA5oAG5TYVdoVDddYAE2AmcFYQ5iU2UEfFFzBysFcAB8VCRfcwJlC34DbFxkAz9SYVQ2Wz5XNAdmAmJTPlZxAy0OYgAyUzRXNlRvXTABNgI7BWYOYVMxBGdRNgdjBTcAdFR/XyYCZgtgA3JcPQMyUnRUdlt2V3QHbAJlUzFWbgNtDjkAaVNlV2BUa10xASYCdwU7DiNTOgRgUTYHagUqAGtUZV81Ai4LOAM1XC4DM1JgVDY=";

    //可以提前从 xdb 文件中加载出来 VectorIndex 数据，然后全局缓存，
    // 每次创建 Searcher 对象的时候使用全局的 VectorIndex 缓存可以减少一次固定的 IO 操作，从而加速查询，减少 IO 压力。
    @Bean(name = "ipSearcherConfig")
    public IpSearcher loadIpConfig(){
        try {
            File file = new File(dbFilePath);
            if(!file.exists()){
//                log.info("本地未找到ip库(建议配置ip.db.path本地路径)，切换到从网络资源下载ip库，下载路径为：{}",dbFilePath);
//                FileUtils.copyByUrl(DOWNLOAD_DB_URL,dbFilePath);
                throw new SoException("请把ipdb目录下ip2region.xdb文件放到："+dbFilePath);
            }

            // 1、从 dbPath 中预先加载 VectorIndex 缓存，并且把这个得到的数据作为全局变量，后续反复使用。
            byte[] vIndex = new byte[0];
            try {
                vIndex = IpSearcher.loadVectorIndexFromFile(dbFilePath);
            } catch (Exception e) {
                log.error("ip config load vectorIndex error.",e);
                throw new SoException("ip config load vectorIndex error："+e.getMessage());
            }

            // 2、使用全局的 vIndex 创建带 VectorIndex 缓存的查询对象。
            IpSearcher searcher = null;
            try {
                searcher = IpSearcher.newWithVectorIndex(dbFilePath, vIndex);
            } catch (Exception e) {
                log.error("ip config load new IpSearcher error.",e);
                throw new SoException("ip config load new IpSearcher error："+e.getMessage());
            }
            log.info("ip config load success.");
            return searcher;
        }catch (Exception e){
            log.error("ip config load error：{}",e.getMessage());
            throw new SoException("ip config load error："+e.getMessage());
        }
    }
}
