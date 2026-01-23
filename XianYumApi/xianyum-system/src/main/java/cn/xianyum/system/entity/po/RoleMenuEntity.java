package cn.xianyum.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

/**
 * 角色菜单关联表(RoleMenu)表实体类
 *
 * @author zhangwei
 * @since 2023-11-01 20:00:35
 */
@Data
@TableName("role_menu")
public class RoleMenuEntity extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 菜单id
     */
    private Long menuId;

}
