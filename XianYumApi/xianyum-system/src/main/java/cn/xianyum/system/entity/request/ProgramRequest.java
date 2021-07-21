package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangwei
 * @date 2020/11/20 20:27
 */
@Data
public class ProgramRequest extends BaseRequest {

    private String id;

    private String beginTime;

    private String endTime;

    private String contactName;

    private String programTitle;

    private Integer status;

    private Integer type;

    private Integer tmallStatus;

    private String programRequirements;

    private Date expectTime;

    private String contactPhone;

    private String tag;

    private String remark;

    private Double money;
}
