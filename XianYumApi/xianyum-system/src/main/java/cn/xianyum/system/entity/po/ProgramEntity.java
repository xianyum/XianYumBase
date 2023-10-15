package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 程序设计(program)
 * @author zhangwei
 * @date 2020/11/20 20:26
 */
@Data
@TableName(value = "program")
public class ProgramEntity extends BaseEntity {

    /** id */
    @TableId(type = IdType.INPUT)
    private String id;

    /** 程序题目 */
    private String programTitle;

    /** 程序要求 */
    private String programRequirements;

    /** 联系人 */
    private String contactName;

    /** 联系方式 */
    private String contactPhone;

    /** 预计完成时间 */
    private Date expectTime;

    /** 程序状态(0: 已完成 1：审核中 2 开发中) */
    private Integer status;

    /** 是否淘系买家(0:淘系买家 1:散客买家) */
    private Integer tmallStatus;

    /** 最终完成时间 */
    private Date completionTime;

    /** 订单类型 0：系统 1：系统+论文 2：论文 */
    private Integer type;

    /** 订单价格 */
    private Double money;

    /** 订单备注 */
    private String remark;

    private String giteeUrl;
}
