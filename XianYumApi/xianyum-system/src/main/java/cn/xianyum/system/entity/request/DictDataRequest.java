package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2023/9/19 20:08
 */
@Data
public class DictDataRequest extends BaseRequest {

    /** 字典标签 */
    private String dictLabel;

    /** 字典类型 */
    private String dictType;

    /** 状态（0正常 1停用） */
    private String status;
}
