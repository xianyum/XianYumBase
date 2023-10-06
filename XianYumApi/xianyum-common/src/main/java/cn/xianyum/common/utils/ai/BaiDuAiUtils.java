package cn.xianyum.common.utils.ai;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.HttpUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.utils.SystemConstantUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Objects;

/**
 * 百度公共utils获取
 * @author zhangwei
 * @date 2023/10/5 20:00
 */
@Component
@Slf4j
public class BaiDuAiUtils {

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.baidu-ai.access_token}")
    private String baiduAiAccessTokenPrefix;

    /** ocr识别-标准版 */
    public static final String OCR_GENERAL_BASIC_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
    private static final String ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";

    /**
     * 获取百度ai accesstoken
     * @param clientId
     * @param clientSecret
     * @return
     */
    public String getAccessToken(String clientId,String clientSecret){
        if(StringUtil.isEmpty(clientId) || StringUtil.isEmpty(clientSecret)){
            String valueByKey = SystemConstantUtils.getValueByKey("baidu_ai_ocr");
            JSONObject systemConstantObject = JSONObject.parseObject(valueByKey);
            clientId = systemConstantObject.getString("clientId");
            clientSecret = systemConstantObject.getString("clientSecret");
        }
        String redisKey = baiduAiAccessTokenPrefix+clientId;
        if(redisUtils.hasKey(redisKey)){
            return redisUtils.getString(redisKey);
        }
        String result = HttpUtils.getHttpInstance().sync(ACCESS_TOKEN_URL)
                .addUrlPara("grant_type","client_credentials")
                .addUrlPara("client_id",clientId)
                .addUrlPara("client_secret",clientSecret)
                .post().getBody().toString();
        JSONObject resultObject = JSONObject.parseObject(result);
        String accessToken = resultObject.getString("access_token");
        Long expiresIn = resultObject.getLong("expires_in");
        redisUtils.set(redisKey,accessToken,expiresIn);
        return accessToken;
    }

    public String getAccessToken(){
        return getAccessToken(null,null);
    }

    /**
     * https://cloud.baidu.com/doc/OCR/s/zk3h7xz52
     * OCR识别标准版
     * @param accessToken
     * @param imageData
     * @return
     */
    public String ocrGeneralBasic(String accessToken,String imageData){
        try {
            if(StringUtil.isEmpty(accessToken)){
                accessToken = this.getAccessToken();
            }
            String result = HttpUtils.getHttpInstance().sync(OCR_GENERAL_BASIC_URL)
                    .addUrlPara("access_token",accessToken)
                    .addUrlPara("image", URLEncoder.encode(imageData, "UTF-8"))
                    .post().getBody().toString();
            String wordsResult = JSONObject.parseObject(result).getString("words_result");
            JSONArray wordsResultArray = JSONObject.parseArray(wordsResult);
            if(Objects.isNull(wordsResultArray)){
                return null;
            }
            JSONObject jsonObject = wordsResultArray.getJSONObject(0);
            log.info("百度AI开放平台识别通用文字结果：{}",wordsResult);
            return jsonObject.getString("words");
        }catch (Exception e){
            throw new SoException(e.getMessage());
        }
    }

}
