package cn.xianyum.system.service;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.system.entity.request.RoleRequest;
import cn.xianyum.system.entity.response.RoleResponse;
import cn.xianyum.common.entity.base.PageResponse;
import java.util.List;
import java.util.Set;

/**
 * 角色管理(Role)service层
 *
 * @author makejava
 * @since 2023-10-31 19:57:15
 */
public interface RoleService{

	PageResponse<RoleResponse> getPage(RoleRequest request);

	RoleResponse getById(Long id);

	Integer save(RoleRequest request);

	Integer update(RoleRequest request);

	Integer deleteById(Long[] ids);

	Integer changeStatus(RoleRequest request);

	Integer changeDataScope(RoleRequest request);

	Integer authorizationMenu(RoleRequest request);

	List<RoleResponse> getList(RoleRequest request);

	Set<String> getRolePermission(String userId);

	List<RoleResponse> getRoleByUserId(String userId);

    void setLoginUserRoleService(LoginUser loginUser);

	void checkRoleIsDelete(Long id);
}
