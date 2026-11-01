package cn.xianyum.system.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志响应
 */
@Data
public class OperLogResponse extends BaseResponse {

    private Long id;

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

    private Date createTime;

    private Long visitCount;
}
