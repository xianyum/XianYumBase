package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2023/9/19 17:30
 */
@Data
public class DictTypeRequest extends BaseRequest {

    /** 字典名称 */
    private String dictName;

    /** 字典类型 */
    private String dictType;

    /** 状态（0正常 1停用） */
    private String status;
}
