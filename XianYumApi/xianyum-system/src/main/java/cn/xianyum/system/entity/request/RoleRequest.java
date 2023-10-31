package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * 角色管理(Role)request请求实体
 *
 * @author makejava
 * @since 2023-10-31 19:57:15
 */
@Data
public class RoleRequest extends BaseRequest {
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

    private String remark;

}
