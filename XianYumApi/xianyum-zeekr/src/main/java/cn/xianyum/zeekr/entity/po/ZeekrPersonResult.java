package cn.xianyum.zeekr.entity.po;

import lombok.Data;

@Data
public class ZeekrPersonResult {

    private Long id;
    private Long projectId;
    private String projectName;
    private Long projectManagerId;
    private String projectManagerName;
    private Integer approvalStatus;
    private String tsTime;
    private String remark;
    private String approvalOpinion;
    private String tsEmployeeNum;
    private String tsEmployeeName;
    private Long tenantId;
    private Long objectVersionNumber;
    /** 时间格式为：yyyy-MM-dd */
    private String dateTime;
}
