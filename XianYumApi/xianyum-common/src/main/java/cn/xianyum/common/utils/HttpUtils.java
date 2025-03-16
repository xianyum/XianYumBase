package cn.xianyum.common.utils;

import cn.xianyum.common.constant.Constants;
import cn.zhxu.okhttps.HTTP;
import cn.zhxu.okhttps.fastjson2.Fastjson2MsgConvertor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * http://okhttps.ejlchina.com/v2/getstart.html#maven
 * @author zhangwei
 * @date 2020-12-4 19:24:32
 */
public class HttpUtils {

    private static volatile HTTP http;

    private HttpUtils() {

    }

    /**
     * 返回Okhttp3实例
     * @return
     */
    public static synchronized HTTP getHttpInstance() {
        if(null == http){
            synchronized (HttpUtils.class){
                if(null == http){
                    http = HTTP.builder().addMsgConvertor(new Fastjson2MsgConvertor())
                            .config((OkHttpClient.Builder builder) -> {
                                // 连接超时时间（默认10秒）
                                builder.connectTimeout(10, TimeUnit.SECONDS);
                                // 写入超时时间（默认10秒）
                                builder.writeTimeout(10, TimeUnit.SECONDS);
                                // 读取超时时间（默认10秒）
                                builder.readTimeout(10, TimeUnit.SECONDS);

                                // 添加默认的请求头
                                builder.addInterceptor(chain -> {
                                    // 获取原始请求
                                    Request originalRequest = chain.request();
                                    // 构建新的请求，并添加自定义的请求头
                                    Request newRequest = originalRequest.newBuilder()
                                            .addHeader(Constants.USER_AGENT_KEY, Constants.USER_AGENT_VALUE)
                                            .build();
                                    // 继续请求
                                    return chain.proceed(newRequest);
                                });
                            })
                            .build();
                }
            }
        }
        return http;
    }

}
