package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 操作日志实体
 */
@Data
@TableName(value = "sys_oper_log")
public class OperLogEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String userName;

    private String module;

    private String operType;

    private String operDesc;

    private String requestUrl;

    private String requestMethod;

    private String ipAddress;

    private String location;

    private String className;

    private String methodName;

    private String requestParams;

    private String responseResult;

    private String errorMsg;

    private Long costTime;

    private Integer status;
}
