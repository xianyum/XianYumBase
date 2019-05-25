package com.base.entity.request;

import lombok.Data;

/***
 * 获取登录提交的参数
 */
@Data
public class UserRequest {
    private Long id;
    private String username;
    private String password;
    private String captcha;
    private String uuid;
    public Integer pageNum;
    public Integer pageSize;
    private Integer permissionStatus; //权限状态标识
}
