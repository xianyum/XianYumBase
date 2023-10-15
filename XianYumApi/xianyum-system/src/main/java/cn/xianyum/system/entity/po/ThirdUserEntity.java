package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 阿里用户信息和本系统用户实体
 * @author zhangwei
 * @date 2019/11/9 17:36
 * @desc
 */
@Data
@TableName(value = "user_third")
public class ThirdUserEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String userId;

    /**
     * 阿里用户userId
     */
    private String aliUserId;

    /**
     * qq用户userId
     */
    private String qqUserId;

}
