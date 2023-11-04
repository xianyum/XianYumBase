package cn.xianyum.common.entity.base;

import lombok.Data;
import java.util.Date;


/**
 * @author zhangwei
 * @date 2023/10/13 21:12
 */
@Data
public class BaseResponse {

    private String createBy;

    private String createByName;

    private Date createTime;

    private String updateBy;

    private String updateByName;

    private Date updateTime;

    /** 参数签名：防止篡改数据 */
    public String signature;

}
