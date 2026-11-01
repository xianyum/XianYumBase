package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * 操作日志查询请求
 */
@Data
public class OperLogRequest extends BaseRequest {

    private String startTime;

    private String endTime;

    private String userName;

    private String module;

    private String operType;

    private Integer status;

    private String requestUrl;

    private String queryTime;
}
