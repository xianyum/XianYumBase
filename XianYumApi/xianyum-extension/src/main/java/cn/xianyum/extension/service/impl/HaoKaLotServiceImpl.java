package cn.xianyum.extension.service.impl;

import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.extension.entity.po.HaoKaLotArticleEntity;
import cn.xianyum.extension.entity.request.HaoKaLotProductRequest;
import cn.xianyum.extension.entity.response.HaoKaLotProductResponse;
import cn.xianyum.extension.service.HaoKaLotService;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.common.utils.ai.BaiDuAiUtils;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.zhxu.okhttps.OkHttps;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2023/10/6 18:28
 */
@Service
@Slf4j
public class HaoKaLotServiceImpl implements HaoKaLotService {


    private final String IMAGE_CODE_URL = "https://haokaapi.lot-ml.com/api/User/ImgCode";
    private final String LOGIN_URL = "https://haokaapi.lot-ml.com/api/User/Login";

    private final String ARTICLE_URL = "https://haokaapi.lot-ml.com/api/Article/QueryList?page=1&limit=10";

    private final String PRODUCT_URL = "https://haokaapi.lot-ml.com/api/Products/Query";
    private final String HAO_KA_LOT_HOME_URL = "https://haokawx.lot-ml.com/Product/Index/134856";

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private BaiDuAiUtils baiDuAiUtils;

    @Autowired
    private MessageSender messageSender;

    @Value("${redis.analysis.hao_kao_lot.token}")
    private String haoKaLotTokenPrefix;

    // 记录推送的起始位置
    @Value("${redis.analysis.hao_kao_lot.article_index}")
    private String haoKaLotArticleIndexPrefix;

    @Override
    public ReturnT pushArticleMessage(Map<String, String> jobMapParams, SchedulerTool tool) {
        List<HaoKaLotArticleEntity> haoKaLotArticleEntities = this.getHaoKaLotArticleList();
        Long articleIndex;
        if(redisUtils.hasKey(haoKaLotArticleIndexPrefix)){
            articleIndex = Long.valueOf(redisUtils.getString(haoKaLotArticleIndexPrefix));
        } else {
            articleIndex = 0L;
        }
        Long finalArticleIndex = articleIndex;
        List<HaoKaLotArticleEntity> fiterList = haoKaLotArticleEntities.stream().filter(p -> p.getArticleID() > finalArticleIndex).collect(Collectors.toList());
        for(HaoKaLotArticleEntity haoKaLotArticleEntity : fiterList){
            Long articleId = haoKaLotArticleEntity.getArticleID();
            if(articleId > articleIndex){
                articleIndex = articleId;
            }
            Map<String,Object> content = new LinkedHashMap<>();
            content.put("消息内容：",haoKaLotArticleEntity.getInfo());
            content.put("原始Id：",haoKaLotArticleEntity.getArticleID());
            MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
            messageSenderEntity.setFormUrl(HAO_KA_LOT_HOME_URL);
            messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
            messageSender.sendAsyncMessage(MessageCodeEnums.HAO_KA_LOT_ARTICLE_NOTIFY.getMessageCode(),messageSenderEntity);
        }
        redisUtils.set(haoKaLotArticleIndexPrefix,String.valueOf(articleIndex));
        return ReturnT.SUCCESS;
    }

    /**
     * 获取172号卡登录token
     *
     * @return
     */
    @Override
    public String getAccessTokenByLogin() {
        String valueByKey = SystemConstantUtils.getValueByKey(SystemConstantKeyEnum.HAO_KA_LOT_LOGIN_INFO);
        JSONObject systemConstantObject = JSONObject.parseObject(valueByKey);
        String userName = systemConstantObject.getString("userName");
        String password = systemConstantObject.getString("password");
        String redisKey = haoKaLotTokenPrefix+userName;
        if(redisUtils.hasKey(redisKey)){
            return redisUtils.getString(redisKey);
        }
        // 获取验证码
        String imageCodeStr = HttpUtils.getHttpInstance().sync(IMAGE_CODE_URL).get().getBody().toString();
        JSONObject imageCodeObject = JSONObject.parseObject(imageCodeStr);
        String imageCodeData = imageCodeObject.getString("data");
        // 识别验证码
        String imageCode = baiDuAiUtils.ocrGeneralBasic(null, imageCodeData);
        // 登录账号获取token
        JSONObject requestObject = new JSONObject();
        requestObject.put("UserName",userName);
        requestObject.put("PassWord",password);
        requestObject.put("Code",imageCode);
        String loginResultStr = HttpUtils.getHttpInstance().sync(LOGIN_URL).bodyType(OkHttps.JSON)
                .setBodyPara(requestObject.toJSONString()).post().getBody().toString();
        JSONObject loginResultObject = JSONObject.parseObject(loginResultStr);
        String token = JSONObject.parseObject(loginResultObject.getString("data")).getString("token");
        log.info("172号卡系统生成token,{}",token);
        if(StringUtil.isNotEmpty(token)){
            redisUtils.setMin(redisKey,token,120);
        }
        return token;
    }

    /**
     * 获取172号通知
     *
     * @return
     */
    @Override
    public List<HaoKaLotArticleEntity> getHaoKaLotArticleList() {
        String token = "bearer "+this.getAccessTokenByLogin();
        String articleJsonStr = HttpUtils.getHttpInstance().sync(ARTICLE_URL).addHeader("Authorization",token).get().getBody().toString();
        if(StringUtil.isEmpty(articleJsonStr)){
            log.error("172号卡系统token可能已经失效,{}",token);
            throw new SoException("172号卡系统token可能已经失效："+token);
        }
        List<HaoKaLotArticleEntity> haoKaLotArticleEntities = JSONObject.parseObject(JSONObject.parseObject(articleJsonStr).getString("data"), new TypeReference<List<HaoKaLotArticleEntity>>() {
        });
        return haoKaLotArticleEntities;
    }

    /**
     * 分页获取172号卡商品列表
     *
     * @param request
     * @return
     */
    @Override
    public PageResponse<HaoKaLotProductResponse> getPage(HaoKaLotProductRequest request) {

        String token = "bearer "+this.getAccessTokenByLogin();
        String result = HttpUtils.getHttpInstance().sync(PRODUCT_URL)
                .addHeader("Authorization",token)
                .addUrlPara("page",request.getPageNum())
                .addUrlPara("limit", request.getPageSize())
                .addUrlPara("ProductName", request.getProductName())
                .addUrlPara("Operator", request.getOperator())
                .get().getBody().toString();
        JSONObject resultObject = JSONObject.parseObject(result);
        Long count = resultObject.getLong("count");
        List<HaoKaLotProductResponse> data = JSONArray.parseArray(resultObject.getString("data"), HaoKaLotProductResponse.class);
        return PageResponse.of(count,data);
    }
}
