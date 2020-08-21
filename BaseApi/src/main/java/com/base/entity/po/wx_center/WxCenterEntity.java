package com.base.entity.po.wx_center;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * @author zhangwei
 * @date 2019/9/29 13:17
 */
@Data
@TableName(value = "wx_push_center")
public class WxCenterEntity {


    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 关注应用的appKey
     */
    private String appKey;

    /**
     * 应用名字
     */
    private String appName;

    /**
     * 用户关注渠道，scan表示扫码关注
     */
    private String source;

    /**
     * 用户关注时间
     */
    private Date createTime;

}
