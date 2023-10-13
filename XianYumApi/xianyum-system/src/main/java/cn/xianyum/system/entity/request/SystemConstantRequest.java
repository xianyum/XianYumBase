package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * 系统常用常量(system_constant)
 *
 */
@Data
public class SystemConstantRequest extends BaseRequest {


    /** id */
    private String id;

    /** constantKey */
    private String constantKey;

    /** constantValue */
    private String constantValue;

    /** 描述 */
    private String constantDescribe;

    /** 0:公用 1：私有 */
    private Integer constantVisible;

}
