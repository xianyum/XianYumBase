package com.base.entity.request;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2019/6/20 14:18
 */
@Data
public class LogRequest extends BaseRequest {

    private String nameOrDesc;

    private String startTime;

    private String endTime;

    private String queryTime;
}
