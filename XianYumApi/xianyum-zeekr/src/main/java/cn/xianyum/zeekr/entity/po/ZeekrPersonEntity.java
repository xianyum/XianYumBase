package cn.xianyum.zeekr.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * @author zhangwei
 * @date 2022/8/7 13:45
 */
@Data
@TableName(value = "zeekr_person")
public class ZeekrPersonEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String loginName;

    private String projectId;

    private String projectName;

    private String projectManagerId;

    private String projectManagerName;

    private String employeeName;

    private String employeeNum;

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    private String createUserId;
}
