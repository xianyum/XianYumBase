package cn.xianyum.proxy.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 客户端管理(proxy)
 *
 */
@Data
@TableName(value = "proxy")
public class ProxyEntity extends BaseEntity {

    /** 客户端秘钥 */
	@TableId(type = IdType.INPUT)
    private String id;

    /** 客户端名称 */
    private String name;

    /** 登录次数 */
    private Integer loginCount;

    /** 是否通知，0：不通知 1：通知 */
    private Integer notify;

    /** 绑定用户ID */
    private String bindUserId;

}
