package cn.xianyum.extension.entity.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 高德地图逆地理编码响应
 * @author xianyum
 * @date 2026/8/27
 */
@Data
public class AmapRegeoResponse {

    /** 状态码 1成功，0失败 */
    private String status;

    /** 逆地理编码结果 */
    @JSONField(name = "regeocode")
    private Regeocode regeoCode;

    /** 返回信息 */
    private String info;

    /** 状态码 10000正常 */
    @JSONField(name = "infocode")
    private String infoCode;

    @Data
    public static class Regeocode {

        /** 格式化完整地址 */
        @JSONField(name = "formatted_address")
        private String formattedAddress;

        /** 地址组件 */
        private AddressComponent addressComponent;
    }

    @Data
    public static class AddressComponent {
        /** 国家 */
        private String country;

        /** 省份 */
        private String province;

        /** 城市，部分场景返回数组 */
        private List<String> city;

        /** 区县 */
        private String district;

        /** 乡镇/街道 */
        private String township;

        /** 城市编码 */
        @JSONField(name = "citycode")
        private String cityCode;

        /** 区域编码 */
        @JSONField(name = "adcode")
        private String adCode;

        /** 镇编码 */
        @JSONField(name = "towncode")
        private String townCode;

        /** 街道门牌号 */
        @JSONField(name = "streetNumber")
        private StreetNumber streetNumber;

        /** 楼宇信息 */
        @JSONField(name = "building")
        private Building building;

        /** 社区信息 */
        @JSONField(name = "neighborhood")
        private Neighborhood neighborhood;

        /** 商圈列表 */
        @JSONField(name = "businessAreas")
        private List<BusinessArea> businessAreas;
    }

    @Data
    public static class StreetNumber {
        /** 门牌号 */
        private String number;
        /** 经纬度 lon,lat */
        private String location;
        /** 方向 */
        private String direction;
        /** 距离 */
        private String distance;
        /** 街道名称 */
        private String street;
    }

    @Data
    public static class Building {
        /** 楼宇名称 */
        private String name;
        /** 类型 */
        private String type;
    }

    @Data
    public static class Neighborhood {
        /** 小区名称 */
        private String name;
        /** 类型 */
        private String type;
    }

    @Data
    public static class BusinessArea {
        /** 商圈名称 */
        private String name;
        /** 商圈id */
        private String id;
        /** 商圈坐标 lon,lat */
        private String location;
    }
}
