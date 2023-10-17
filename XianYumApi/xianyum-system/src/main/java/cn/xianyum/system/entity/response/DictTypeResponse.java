package cn.xianyum.system.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2023/9/19 17:24
 */
@Data

public class DictTypeResponse extends BaseResponse {

    private Long id;

    /** 字典名称 */
    private String dictName;

    /** 字典类型 */
    private String dictType;

    /** 状态（0正常 1停用） */
    private String status;

    private String remark;

}
