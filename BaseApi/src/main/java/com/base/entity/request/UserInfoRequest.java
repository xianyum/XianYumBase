package com.base.entity.request;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfoRequest {
    private Long id;
    private String username;
    private String password;
    private Integer permissionStatus;
    private String permissionName;
    private Integer status;
    private String salt;
    private Integer delTag;
    private Date createTime;
}
