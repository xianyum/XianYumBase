package cn.xianyum.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

/**
 * 用户权限关联表(UserRole)表实体类
 *
 * @author zhangwei
 * @since 2023-11-02 11:05:05
 */
@Data
@TableName("user_role")
public class UserRoleEntity extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 角色id
     */
    private Long roleId;

}
