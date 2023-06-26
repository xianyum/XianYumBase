package cn.xianyum.common.utils;

import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.fastjson.FastjsonMsgConvertor;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

/**
 * http://okhttps.ejlchina.com/v2/getstart.html#maven
 * @author zhangwei
 * @date 2020-12-4 19:24:32
 */
public class HttpUtils {

    private volatile static HTTP http;

    private HttpUtils() {

    }

    /**
     * 返回Okhttp3实例
     * @return
     */
    public static synchronized HTTP getHttpInstance() {

        if(null == http){
            synchronized (HTTP.class){
                if(null == http){
                    http = HTTP.builder().addMsgConvertor(new FastjsonMsgConvertor())
                            .config((OkHttpClient.Builder builder) -> {
                                // 连接超时时间（默认10秒）
                                builder.connectTimeout(1, TimeUnit.SECONDS);
                                // 写入超时时间（默认10秒）
                                builder.writeTimeout(1, TimeUnit.SECONDS);
                                // 读取超时时间（默认10秒）
                                builder.readTimeout(1, TimeUnit.SECONDS);
                            })
                            .build();
                }
            }
        }
        return http;
    }

}
