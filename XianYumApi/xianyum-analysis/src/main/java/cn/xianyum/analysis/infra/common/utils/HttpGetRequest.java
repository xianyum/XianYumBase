package cn.xianyum.analysis.infra.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.geccocrawler.gecco.request.AbstractHttpRequest;

/**
 * @author zhangwei
 * @date 2024/2/17 18:30
 */
public class HttpGetRequest extends AbstractHttpRequest {

    private static final long serialVersionUID = 6105458424891960971L;

    public HttpGetRequest() {
        super();
    }

    public HttpGetRequest(String url) {
        super(url);
    }

    public static HttpGetRequest fromJson(JSONObject request) {
        return (HttpGetRequest) JSON.toJavaObject(request, HttpGetRequest.class);
    }
}
