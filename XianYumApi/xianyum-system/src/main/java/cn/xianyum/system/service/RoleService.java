package cn.xianyum.system.service;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.system.entity.request.RoleRequest;
import cn.xianyum.system.entity.response.RoleResponse;
import cn.xianyum.common.entity.base.PageResponse;
import java.util.List;

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

	LoginUser getCurrentUserRolePermission(String userId);

	void clearRoleCacheByUserId(String userId);

	void checkRoleIsDelete(Long id);
}
