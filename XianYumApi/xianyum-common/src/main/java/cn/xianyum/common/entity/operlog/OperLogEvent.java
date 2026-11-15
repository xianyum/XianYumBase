package cn.xianyum.common.entity.operlog;

import lombok.Data;

/**
 * 操作日志事件
 * 框架层发布，业务层监听后落库
 */
@Data
public class OperLogEvent {

    private String userId;

    private String userName;

    private String module;

    private String operType;

    private String operDesc;

    private String requestUrl;

    private String requestMethod;

    private String ipAddress;

    private String location;

    private String className;

    private String methodName;

    private String requestParams;

    private String responseResult;

    private String errorMsg;

    private Long costTime;

    private Integer status;
}
