package cn.xianyum.zeekr.entity.response;

import lombok.Data;
import java.util.Date;

/**
 * zeekr_person
 *
 */
@Data
public class ZeekrPersonResponse{

    /** id */
    private Long id;

    /** loginName */
    private String loginName;

    /** projectId */
    private String projectId;

    /** projectName */
    private String projectName;

    /** projectManagerId */
    private String projectManagerId;

    /** projectManagerName */
    private String projectManagerName;

    /** employeeNum */
    private String employeeNum;

    /** employeeName */
    private String employeeName;

    /** createTime */
    private Date createTime;

    /** createUserId */
    private String createUserId;


}
