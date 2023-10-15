package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统常用参数
 * @author zhangwei
 * @date 2020/11/3 18:29
 */
@Data
@TableName(value = "system_constant")
public class SystemConstantEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String constantKey;

    private String constantValue;

    /** 描述 */
    private String constantDescribe;

    /** 0:公用 1：私有 */
    private Integer constantVisible;
}
