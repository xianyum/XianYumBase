package cn.xianyum.extension.infra.amap;

import cn.hutool.core.text.StrPool;
import cn.xianyum.common.config.AmapProperties;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.HttpUtils;
import cn.xianyum.extension.entity.response.AmapRegeoResponse;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 高德地图服务
 * 集成高德地图各类API：逆地理编码、地理编码、路径规划、POI搜索等
 * @author xianyum
 * @date 2026/8/27 23:12
 */
@Component
@Slf4j
public class AmapService {

    @Resource
    private AmapProperties amapProperties;

    /**
     * 逆地理编码：通过经纬度查询地址信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return 逆地理编码响应
     */
    public AmapRegeoResponse getAddressByLocation(String longitude, String latitude) {
        String location = longitude + StrPool.COMMA + latitude;
        String url = amapProperties.getBaseUrl() + amapProperties.getRegeoPath();
        String result = HttpUtils.getHttpInstance().sync(url)
                .addUrlPara("key", amapProperties.getKey())
                .addUrlPara("location", location)
                .get()
                .getBody()
                .toString();
        AmapRegeoResponse amapRegeoResponse = JSONObject.parseObject(result, AmapRegeoResponse.class);
        if (!"1".equals(amapRegeoResponse.getStatus())) {
            log.error("高德逆地理编码接口返回失败：{}", result);
            throw new SoException("高德逆地理编码接口返回失败：" + amapRegeoResponse.getInfo());
        }
        return amapRegeoResponse;
    }

    /**
     * 逆地理编码：通过经纬度获取格式化地址文本
     * @param longitude 经度
     * @param latitude 纬度
     * @return 格式化地址
     */
    public String getFormattedAddress(String longitude, String latitude) {
        AmapRegeoResponse response = getAddressByLocation(longitude, latitude);
        if (response != null && response.getRegeoCode() != null) {
            return response.getRegeoCode().getFormattedAddress();
        }
        return null;
    }
}
