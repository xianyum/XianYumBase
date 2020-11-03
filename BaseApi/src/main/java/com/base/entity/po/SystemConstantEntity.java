package com.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统常用参数
 * @author zhangwei
 * @date 2020/11/3 18:29
 */
@Data
@TableName(value = "system_constant")
public class SystemConstantEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String constantKey;

    private String constantValue;

    /** 描述 */
    private String constantDescribe;

    /** 0:公用 1：私有 */
    private Integer constantVisible;

    private Date createTime;
}
