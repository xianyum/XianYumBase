package cn.xianyum.system.entity.po;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import lombok.Data;
import java.util.Date;

/**
 * @author zhangwei
 * @date 2020/11/21 14:59
 */
@Data
@HeadRowHeight(15)
@HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
public class ExportProgramEntity {

    /** 程序题目 */
    @ExcelProperty("程序题目")
    @ColumnWidth(25)
    private String programTitle;

    /** 程序要求 */
    @ExcelProperty("程序要求")
    @ColumnWidth(16)
    private String programRequirements;

    /** 程序状态(0: 已完成 1：审核中 2 开发中) */
    @ExcelProperty("订单状态")
    @ColumnWidth(16)
    private String statusStr;


    /** 订单类型 0：系统 1：系统+论文 2：论文 */
    @ExcelProperty("订单类型")
    @ColumnWidth(16)
    private String typeStr;

    /** 订单价格 */
    @ExcelProperty("订单价格")
    @ColumnWidth(16)
    private Double money;

    /** 联系人 */
    @ExcelProperty("联系人")
    @ColumnWidth(16)
    private String contactName;

    /** 联系方式 */
    @ExcelProperty("联系方式")
    @ColumnWidth(16)
    private String contactPhone;

    /** 是否淘系买家(0:淘系买家 1:散客买家) */
    @ExcelProperty("是否淘系买家")
    @ColumnWidth(16)
    private String tmallStatusStr;

    /** 预计完成时间 */
    @ExcelProperty(value = "预计完成时间")
    @ColumnWidth(20)
    private Date expectTime;

    /** 最终完成时间 */
    @ExcelProperty("最终完成时间")
    @ColumnWidth(20)
    private Date completionTime;

    /** 创建人名称 */
    @ExcelProperty("创建人名称")
    @ColumnWidth(16)
    private String createUserName;

    /** 创建时间 */
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private Date createTime;

    @ExcelProperty("更新人名称")
    @ColumnWidth(16)
    private String updateUserName;

    @ExcelProperty("更新时间")
    @ColumnWidth(20)
    private Date updateTime;

    @ExcelIgnore
    private Integer tmallStatus;

    @ExcelIgnore
    private Integer status;

    public String getTmallStatusStr() {
        if(0 == tmallStatus){
            return "淘系买家";
        }else if(1 == tmallStatus){
            return "散客买家";
        }
        return tmallStatusStr;
    }

    public String getStatusStr() {
        if(0 == status){
            return "已完成";
        }else if(1 == status){
            return "审核中";
        }else if(2 == status){
            return "开发中";
        }
        return statusStr;
    }

    public String getTypeStr() {
        if(0 == status){
            return "系统";
        }else if(1 == status){
            return "系统+论文";
        }else if(2 == status){
            return "论文";
        }
        return typeStr;
    }
}
