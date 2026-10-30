package cn.xianyum.framwork.satoken.handler;

import cn.dev33.satoken.stp.StpInterface;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.UserCacheHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口处理类
 * @author xianyum
 * @date 2026/10/29 21:54
 */
public class StpInterfaceHandler implements StpInterface {

    /**
     * 返回指定账号id所拥有的权限码集合
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 该账号id具有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser userById = UserCacheHelper.selectUserById(loginId.toString());
        return new ArrayList<>(userById.getPermissions());
    }

    /**
     * 返回指定账号id所拥有的角色标识集合
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 该账号id具有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser userById = UserCacheHelper.selectUserById(loginId.toString());
        return new ArrayList<>(userById.getRoles());
    }
}
