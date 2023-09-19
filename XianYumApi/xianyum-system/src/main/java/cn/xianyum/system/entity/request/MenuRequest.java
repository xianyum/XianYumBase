package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2023/9/19 19:04
 */
@Data
public class MenuRequest extends BaseRequest {

    /** 菜单名称 */
    private String menuName;

    /** 显示状态（0显示 1隐藏） */
    private String visible;

    /** 菜单状态（0正常 1停用） */
    private String status;
}
