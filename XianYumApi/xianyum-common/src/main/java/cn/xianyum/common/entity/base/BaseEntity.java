package cn.xianyum.common.entity.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.util.Date;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2023/10/13 17:11
 */
@Data
public abstract class BaseEntity {

    public static final String CREATE_BY_COLUMN ="create_by";


    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private String createByName;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateByName;
}
