package cn.xianyum.system.entity.po;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

/**
 * 角色管理(Role)表实体类
 *
 * @author makejava
 * @since 2023-10-31 19:57:15
 */
@Data
@TableName("role")
public class RoleEntity extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleCode;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 角色状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 数据范围（1：全部数据权限 2：仅本人数据权限 3：游客数据权限）
     */
    private String dataScope;

}
