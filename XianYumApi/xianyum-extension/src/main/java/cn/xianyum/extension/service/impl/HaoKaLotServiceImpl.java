package cn.xianyum.extension.service.impl;

import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.extension.entity.po.HaoKaLotArticleEntity;
import cn.xianyum.extension.entity.request.HaoKaLotProductRequest;
import cn.xianyum.extension.entity.response.HaoKaLotProductResponse;
import cn.xianyum.extension.service.HaoKaLotService;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.utils.ai.BaiDuAiUtils;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
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



    @Override
    public ReturnT pushArticleMessage(Map<String, String> jobMapParams, SchedulerTool tool) {
        List<HaoKaLotArticleEntity> haoKaLotArticleEntities = this.getHaoKaLotArticleList();
        Long articleIndex;
        if(redisUtils.hasKey(RedisKeyEnum.ANALYSIS_HAO_KAO_LOT_ARTICLE_INDEX.getKey())){
            articleIndex = Long.valueOf(redisUtils.getString(RedisKeyEnum.ANALYSIS_HAO_KAO_LOT_ARTICLE_INDEX.getKey()));
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
        redisUtils.set(RedisKeyEnum.ANALYSIS_HAO_KAO_LOT_ARTICLE_INDEX.getKey(),String.valueOf(articleIndex));
        return ReturnT.SUCCESS;
    }

    /**
     * 获取172号卡登录token
     *
     * @return
     */
    @Override
    public String getAccessTokenByLogin() {
        JSONObject systemConstantObject = SystemConstantUtils.getValueObjectByKey(SystemConstantKeyEnum.HAO_KA_LOT_LOGIN_INFO);
        String userName = systemConstantObject.getString("userName");
        String password = systemConstantObject.getString("password");
        String redisKey = RedisKeyEnum.ANALYSIS_HAO_KAO_LOT_TOKEN.getKey()+userName;
        if(redisUtils.hasKey(redisKey)){
            return redisUtils.getString(redisKey);
        }
        try {
            String imageCodeStr = HttpUtils.get(IMAGE_CODE_URL);
            JSONObject imageCodeObject = JSONObject.parseObject(imageCodeStr);
            String imageCodeData = imageCodeObject.getString("data");
            String imageCode = baiDuAiUtils.ocrGeneralBasic(null, imageCodeData);
            JSONObject requestObject = new JSONObject();
            requestObject.put("UserName",userName);
            requestObject.put("PassWord",password);
            requestObject.put("Code",imageCode);
            String loginResultStr = HttpUtils.postJson(LOGIN_URL, requestObject);
            JSONObject loginResultObject = JSONObject.parseObject(loginResultStr);
            String token = JSONObject.parseObject(loginResultObject.getString("data")).getString("token");
            log.info("172号卡系统生成token,{}",token);
            if(StrUtil.isNotEmpty(token)){
                redisUtils.setMin(redisKey,token,120);
            }
            return token;
        } catch (Exception e) {
            throw new SoException("172号卡登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取172号通知
     *
     * @return
     */
    @Override
    public List<HaoKaLotArticleEntity> getHaoKaLotArticleList() {
        try {
            String token = "bearer "+this.getAccessTokenByLogin();
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", token);
            String articleJsonStr = HttpUtils.get(ARTICLE_URL, headers);
            if(StrUtil.isEmpty(articleJsonStr)){
                log.error("172号卡系统token可能已经失效,{}",token);
                throw new SoException("172号卡系统token可能已经失效："+token);
            }
            List<HaoKaLotArticleEntity> haoKaLotArticleEntities = JSONObject.parseObject(JSONObject.parseObject(articleJsonStr).getString("data"), new TypeReference<List<HaoKaLotArticleEntity>>() {
            });
            return haoKaLotArticleEntities;
        } catch (Exception e) {
            throw new SoException("获取172号卡文章列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取172号卡商品列表
     *
     * @param request
     * @return
     */
    @Override
    public PageResponse<HaoKaLotProductResponse> getPage(HaoKaLotProductRequest request) {
        try {
            String token = "bearer "+this.getAccessTokenByLogin();
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", token);
            StringBuilder urlBuilder = new StringBuilder(PRODUCT_URL);
            urlBuilder.append("?page=").append(request.getPageNum());
            urlBuilder.append("&limit=").append(request.getPageSize());
            if(StrUtil.isNotBlank(request.getProductName())){
                urlBuilder.append("&ProductName=").append(request.getProductName());
            }
            if(StrUtil.isNotBlank(request.getOperator())){
                urlBuilder.append("&Operator=").append(request.getOperator());
            }
            String result = HttpUtils.get(urlBuilder.toString(), headers);
            JSONObject resultObject = JSONObject.parseObject(result);
            Long count = resultObject.getLong("count");
            List<HaoKaLotProductResponse> data = JSONArray.parseArray(resultObject.getString("data"), HaoKaLotProductResponse.class);
            return PageResponse.of(count,data);
        } catch (Exception e) {
            throw new SoException("获取172号卡商品列表失败: " + e.getMessage());
        }
    }
}
