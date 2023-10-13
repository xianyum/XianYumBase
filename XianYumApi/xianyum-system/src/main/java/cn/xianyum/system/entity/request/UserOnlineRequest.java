package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2021/1/28 22:02
 */
@Data
public class UserOnlineRequest extends BaseRequest {

    /**
     * 会话编号
     */
    private String token;


    /**
     * 用户名
     */
    private String username;

    /**
     * 登录系统（用来区分外部系统）
     */
    private String loginSystem;
}
