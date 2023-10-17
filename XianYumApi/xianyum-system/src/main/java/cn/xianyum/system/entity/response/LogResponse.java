package cn.xianyum.system.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2020/8/23 12:56
 */
@Data
public class LogResponse extends BaseResponse {

    private String time;

    private int visitCount;

    private Long id;
    //用户名
    private String username;
    //用户操作
    private String operation;
    //请求方法
    private String method;
    //请求参数
    private String params;
    //IP地址
    private String ip;
    //IP地点
    private String ipInfo;
}
