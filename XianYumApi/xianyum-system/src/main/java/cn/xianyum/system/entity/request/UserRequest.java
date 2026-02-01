package cn.xianyum.system.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import java.util.List;


/***
 * 获取登录提交的参数
 */
@Data
public class UserRequest extends BaseRequest {

    private String id;
    @NotBlank(message="账号不能为空")
    private String username;//登录账号
    private String password;//登录密码
    @NotBlank(message="手机号不能为空")
    private String mobile;
    @NotBlank(message="邮箱不能为空")
    @Email(message="邮箱格式不正确")
    private String email;
    private Integer status;//状态吗  1：允许登录 0：禁止登录
    private String userSource;//用户来源
    private Integer delTag;//删除标记


    private Integer sex;

    /**
     * 用户权限
     */
    @NotNull(message="用户权限不能为空")
    private List<Long> roleIds;

    /**
     * 用户名称
     */
    @NotBlank(message="用户名称不能为空")
    private String nickName;
}
