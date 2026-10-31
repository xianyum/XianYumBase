package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

/**
 * 在线用户实体
 * @author zhangwei
 * @date 2021/1/28 22:00
 */
@Data
public class UserOnlineEntity extends BaseEntity {

    /**
     * 主键id
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 会话编号
     */
    private String token;


    /**
     * 用户名
     */
    private String username;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录来源
     */
    private String loginType;

    /**
     * 过期时间，单位秒
     */
    private String expireStr;
}
