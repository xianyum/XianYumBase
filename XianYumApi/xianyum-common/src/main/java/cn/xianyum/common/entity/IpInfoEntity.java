package cn.xianyum.common.entity;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2020/4/1 13:40
 */
@Data
public class IpInfoEntity {

    /** 国家 */
    private String country;

    /** 省份 */
    private String prov;

    /** 城市 */
    private String city;

    /** isp */
    private String isp;

    /** 赞助 */
    private String support;

    /** email */
    private String email;

    private String ip;
}
