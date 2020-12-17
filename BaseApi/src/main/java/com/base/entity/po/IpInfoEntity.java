package com.base.entity.po;

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

    /** 备注 */
    private String remark;

    private String ip;
}
