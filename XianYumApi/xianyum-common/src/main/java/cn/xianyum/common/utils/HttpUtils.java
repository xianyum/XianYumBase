package cn.xianyum.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.constant.Constants;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class HttpUtils {
    /**
     * JSON Media Type
     */
    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    // 私有构造函数，防止外部实例化
    private HttpUtils() {}
    // OkHttp 客户端，volatile防止指令重排
    private static volatile OkHttpClient client;

    /**
     * 双重检查锁懒加载获取OkHttpClient
     */
    private static OkHttpClient getClient() {
        if (client == null) {
            synchronized (HttpUtils.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build();
                }
            }
        }
        return client;
    }

    /**
     * GET 返回原始字符串（不带请求头）
     */
    public static String get(String url) throws IOException {
        return get(url, (Map<String, String>) null);
    }
    /**
     * GET 返回原始字符串
     */
    public static String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).get();
        addHeaders(builder, headers);
        Request request = builder.build();
        try (Response response = getClient().newCall(request).execute()) {
            assertResponseSuccess(response);
            ResponseBody body = response.body();
            return body == null ? null : body.string();
        }
    }
    /**
     * GET 反序列化为普通Bean（不带请求头）
     */
    public static <T> T get(String url, Class<T> clazz) throws IOException {
        return get(url, null, clazz);
    }
    /**
     * GET 反序列化为普通Bean
     */
    public static <T> T get(String url, Map<String, String> headers, Class<T> clazz) throws IOException {
        String json = get(url, headers);
        return JSON.parseObject(json, clazz);
    }
    /**
     * GET 泛型返回（List<T> / Result<T>）
     */
    public static <T> T get(String url, TypeReference<T> typeRef) throws IOException {
        return get(url, null, typeRef);
    }
    /**
     * GET 泛型返回（List<T> / Result<T>）
     */
    public static <T> T get(String url, Map<String, String> headers, TypeReference<T> typeRef) throws IOException {
        String json = get(url, headers);
        return JSON.parseObject(json, typeRef);
    }
    /**
     * POST JSON 返回原始字符串（不带请求头）
     */
    public static String postJson(String url, Object params) throws IOException {
        return postJson(url, null, params);
    }
    /**
     * POST JSON 返回原始字符串
     */
    public static String postJson(String url, Map<String, String> headers, Object params) throws IOException {
        String jsonStr = params == null ? StrPool.EMPTY_JSON : (params instanceof String ? (String) params : JSON.toJSONString(params));
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonStr);
        Request.Builder builder = new Request.Builder().url(url).post(body);
        addHeaders(builder, headers);
        Request request = builder.build();
        try (Response response = getClient().newCall(request).execute()) {
            assertResponseSuccess(response);
            ResponseBody responseBody = response.body();
            return responseBody == null ? null : responseBody.string();
        }
    }
    /**
     * POST JSON 反序列化为普通Bean（不带请求头）
     */
    public static <T> T postJson(String url, Object params, Class<T> clazz) throws IOException {
        return postJson(url, null, params, clazz);
    }
    /**
     * POST JSON 反序列化为普通Bean
     */
    public static <T> T postJson(String url, Map<String, String> headers, Object params, Class<T> clazz) throws IOException {
        String json = postJson(url, headers, params);
        return JSON.parseObject(json, clazz);
    }
    /**
     * POST JSON 泛型返回（List<T> / Result<T>，不带请求头）
     */
    public static <T> T postJson(String url, Object params, TypeReference<T> typeRef) throws IOException {
        return postJson(url, null, params, typeRef);
    }
    /**
     * POST JSON 泛型返回（List<T> / Result<T>）
     */
    public static <T> T postJson(String url, Map<String, String> headers, Object params, TypeReference<T> typeRef) throws IOException {
        String json = postJson(url, headers, params);
        return JSON.parseObject(json, typeRef);
    }
    /**
     * 增加请求头
     * @param builder
     * @param headers
     */
    public static void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (CollUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
    }
    /**
     * 校验响应状态码，非2xx抛异常
     */
    public static void assertResponseSuccess(Response response) throws IOException {
        if (!response.isSuccessful()) {
            String respBody = "";
            if(response.body() != null){
                respBody = response.body().string();
            }
            log.error("Http request failed, code={}, msg={}, responseBody={}", response.code(), response.message(), respBody);
            throw new IOException("Http request failed, code=" + response.code() + ", msg=" + response.message());
        }
    }
    /**
     * http链接判断是否为http://开头的url
     * @param url
     * @return
     */
    public static boolean isHttpOnly(String url) {
        return StrUtil.startWithAnyIgnoreCase(url, Constants.HTTP);
    }
    /**
     * http链接判断是否为https://或http://开头的url
     * @param url
     * @return
     */
    public static boolean isHttpOrHttps(String url) {
        return StrUtil.startWithAnyIgnoreCase(url, Constants.HTTP, Constants.HTTPS);
    }
}
