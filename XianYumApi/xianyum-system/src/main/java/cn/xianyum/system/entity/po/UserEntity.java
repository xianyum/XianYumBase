package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/***
 * 用户信息
 */
@Data
@TableName(value = "user")
public class UserEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String username;

    private String password;

    private String mobile;

    private String email;

    private Integer status;

    private Integer delTag;

    private Integer sex;

    private String avatar;

    /**
     * 用户名称
     */
    private String nickName;
}
