package cn.xianyum.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangwei
 * @date 2023/9/19 17:24
 */
@Data
@TableName(value = "dict_type")
public class DictTypeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 字典名称 */
    private String dictName;

    /** 字典类型 */
    private String dictType;

    /** 状态（0正常 1停用） */
    private String status;

    private Date createTime;

    private String remark;
}
