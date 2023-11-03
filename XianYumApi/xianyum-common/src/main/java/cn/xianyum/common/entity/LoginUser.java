package cn.xianyum.common.entity;

import cn.xianyum.common.enums.DataScopeEnum;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/***
 * 用户信息
 */
@Data
public class LoginUser implements UserDetails {

    private String id;

    /**
     * 登录名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱号
     */
    private String email;

    /**
     * 状态码，1：允许登录 0：禁止登录
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 删除标记
     */
    private Integer delTag;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 图像地址
     */
    private String avatar;

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
     * 登录方式 0：账号登录 1：QQ登录 2：支付宝登录
     */
    private int loginType;

    /**
     * 角色编码
     */
    private Set<String> roles;

    /**
     * 权限标识符
     */
    private Set<String> permissions;

    /**
     * 角色对应的数据权限标识符
     */
    private DataScopeEnum dataScopeEnum;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * 账户是否未过期,过期无法验证
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     *
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     *
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用 ,禁用的用户不能身份验证
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }


}
