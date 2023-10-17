package cn.xianyum.system.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * 系统常用常量(system_constant)
 *
 */
@Data
public class SystemConstantResponse extends BaseResponse {

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
