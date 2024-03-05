package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 阿里用户信息和本系统用户实体
 * @author zhangwei
 * @date 2019/11/9 17:36
 * @desc
 */
@Data
@TableName(value = "user_third_relation")
public class UserThirdRelationEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 三方类型
     */
    private Integer thirdType;

}
