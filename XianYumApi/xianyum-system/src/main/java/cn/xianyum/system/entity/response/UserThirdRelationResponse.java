package cn.xianyum.system.entity.response;

import java.util.Date;
import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * (UserThirdRelation)response返回实体
 *
 * @author zhangwei
 * @since 2024-03-05 15:03:57
 */
@Data
public class UserThirdRelationResponse extends BaseResponse {
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 三方用户id
     */
    private String openUserId;

    /**
     * 三方用户名称
     */
    private String openUserName;

    /**
     * 三方类型：0qq1支付宝
     */
    private Integer thirdType;

}
