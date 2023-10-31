package cn.xianyum.system.service;

import cn.xianyum.system.entity.request.RoleRequest;
import cn.xianyum.system.entity.response.RoleResponse;
import cn.xianyum.common.entity.base.PageResponse;

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
}
