package cn.xianyum.common.handler;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;


/**
 * @Description 权限实现类
 * @Author ZhangWei
 * @Date 2023/11/3 9:12
 */
@Service("ps")
public class PermissionHandler {

    /** 所有权限标识 */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 校验用户是否有权限
     * @param permission
     * @return
     */
    public boolean hasPerm(String permission){
        if (StringUtil.isEmpty(permission)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(Objects.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()) ){
            return false;
        }
        return hasPermissions(loginUser.getPermissions(),permission);
    }


    /**
     * 校验用户是否具有以下任意一个权限，|分割
     * @param permissions
     * @return
     */
    public boolean hasAnyPerm(String permissions){
        if (StringUtil.isEmpty(permissions)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(Objects.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()) ){
            return false;
        }
        boolean hasPermission = Arrays.stream(permissions.split("\\|"))
                .anyMatch(permission -> hasPermissions(loginUser.getPermissions(), permission));
        return hasPermission;
    }



    /**
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission) {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(permission.trim());
    }
}
