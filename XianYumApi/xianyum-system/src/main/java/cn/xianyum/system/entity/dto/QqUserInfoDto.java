package cn.xianyum.system.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * QQ用户信息实体
 * https://wiki.connect.qq.com/get_user_info
 * @author zhangwei
 * @date 2020/12/30 10:47
 */
@Data
public class QqUserInfoDto {

    /**
     * 返回码，0表示成功
     */
    private Integer ret;

    /**
     * 提示信息，成功时为空
     */
    private String msg;

    /**
     * 是否丢失（0表示未丢失）
     */
    @JsonProperty("is_lost")
    private Integer isLost;

    /**
     * 用户昵称（下划线风格）
     */
    private String nickname;

    /**
     * 性别（男/女）
     */
    private String gender;

    /**
     * 性别类型（2表示男，1表示女，0未知）
     */
    @JsonProperty("gender_type")
    private Integer genderType;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 出生年份
     */
    private String year;

    /**
     * 用户头像URL（40x40）
     */
    private String figureurl;

    /**
     * 用户头像URL（40x40）
     */
    @JsonProperty("figureurl_1")
    private String figureurl1;

    /**
     * 用户头像URL（100x100）
     */
    @JsonProperty("figureurl_2")
    private String figureurl2;

    /**
     * QQ头像URL（40x40）
     */
    @JsonProperty("figureurl_qq_1")
    private String figureurlQq1;

    /**
     * QQ头像URL（100x100）
     */
    @JsonProperty("figureurl_qq_2")
    private String figureurlQq2;

    /**
     * QQ头像URL（原始尺寸）
     */
    @JsonProperty("figureurl_qq")
    private String figureurlQq;

    /**
     * 是否黄钻会员（0否，1是）
     */
    @JsonProperty("is_yellow_vip")
    private String isYellowVip;

    /**
     * 是否会员（0否，1是）
     */
    private String vip;

    /**
     * 黄钻等级
     */
    @JsonProperty("yellow_vip_level")
    private String yellowVipLevel;

    /**
     * 会员等级
     */
    private String level;

    /**
     * 是否年费黄钻（0否，1是）
     */
    @JsonProperty("is_yellow_year_vip")
    private String isYellowYearVip;

    /**
     * 头像URL（补充字段）
     */
    private String headimgurl;

    /**
     * 联合ID（补充字段）
     */
    private String unionid;

    /**
     * 开放ID（补充字段）
     */
    private String openId;

    /**
     * 用户昵称（驼峰风格，补充字段）
     */
    @JsonProperty("nickName")
    private String nickName;

    /**
     * 头像URL（驼峰风格，补充字段）
     */
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    private String userId;
}
