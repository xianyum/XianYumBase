package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2019/6/20 14:18
 */
@Data
public class LogRequest extends BaseRequest {

    private String startTime;

    private String endTime;

    private String queryTime;

    private String username;
}
