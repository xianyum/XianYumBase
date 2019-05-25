package com.base.entity.request;

import lombok.Data;

@Data
public class LoginPhoneRequest {

    private String phone;

    private String code;

    private String uuid;
}
