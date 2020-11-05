package com.base.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/***
 * 用户信息
 */
@Data
@TableName(value = "user")
public class UserEntity extends Model<UserEntity> {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String salt;
    private String mobile;
    private String email;
    private Integer status;//状态吗  1：允许登录 0：禁止登录
    private Date createTime;//创建时间
    private Integer delTag;//删除标记
    private Integer permission;
}
