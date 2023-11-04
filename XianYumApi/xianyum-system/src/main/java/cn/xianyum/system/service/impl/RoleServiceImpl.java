package cn.xianyum.system.service.impl;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.enums.DataScopeEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.dao.RoleMenuMapper;
import cn.xianyum.system.dao.UserRoleMapper;
import cn.xianyum.system.entity.po.RoleMenuEntity;
import cn.xianyum.system.entity.po.UserRoleEntity;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.common.entity.base.PageResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.xianyum.system.entity.po.RoleEntity;
import cn.xianyum.system.entity.request.RoleRequest;
import cn.xianyum.system.entity.response.RoleResponse;
import cn.xianyum.system.service.RoleService;
import cn.xianyum.system.dao.RoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色管理(Role)service层实现
 *
 * @author makejava
 * @since 2023-10-31 19:57:15
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private RoleMenuMapper roleMenuMapper;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Override
	public PageResponse<RoleResponse> getPage(RoleRequest request) {
		LambdaQueryWrapper<RoleEntity> queryWrapper = Wrappers.<RoleEntity>lambdaQuery()
				.like(StringUtil.isNotEmpty(request.getRoleCode()),RoleEntity::getRoleCode,request.getRoleCode())
				.like(StringUtil.isNotEmpty(request.getRoleName()),RoleEntity::getRoleName,request.getRoleName())
				.eq(Objects.nonNull(request.getStatus()),RoleEntity::getStatus,request.getStatus())
				.orderByAsc(RoleEntity::getRoleSort);
		Page<RoleEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		IPage<RoleEntity> pageResult = roleMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,RoleResponse.class);

	}


	@Override
	public RoleResponse getById(Long id) {
		RoleEntity result = roleMapper.selectById(id);
		RoleResponse response = BeanUtils.copy(result, RoleResponse.class);
		return Objects.isNull(response)?new RoleResponse():response;
	}


	@Override
	public Integer save(RoleRequest request) {
		RoleEntity bean = BeanUtils.copy(request,RoleEntity.class);
		return roleMapper.insert(bean);
	}


	@Override
	public Integer update(RoleRequest request) {
		if(Objects.isNull(request.getId())){
			throw new SoException("id不能为空");
		}
		if(request.getId().equals(1L)){
			throw new SoException("不能更新admin权限！");
		}
		RoleEntity bean = BeanUtils.copy(request,RoleEntity.class);
		return roleMapper.updateById(bean);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer deleteById(Long[] ids) {
	    int resultCount = 0;
		for (Long id : ids){
			if(id.equals(1L)){
				throw new SoException("不能删除admin权限！");
			}
			this.checkRoleIsDelete(id);
			resultCount = roleMapper.deleteById(id)+resultCount;
			LambdaQueryWrapper<RoleMenuEntity> roleMenuEntityLambdaQueryWrapper = Wrappers.<RoleMenuEntity>lambdaQuery()
							.eq(RoleMenuEntity::getRoleId,id);
			roleMenuMapper.delete(roleMenuEntityLambdaQueryWrapper);
		}
		return resultCount;
	}

	@Override
	public Integer changeStatus(RoleRequest request) {
		if(Objects.isNull(request.getId()) || Objects.isNull(request.getStatus())){
			throw new SoException("更新角色状态失败！");
		}
		if(request.getId().equals(1L)){
			throw new SoException("不能更新admin权限！");
		}
		RoleEntity bean = new RoleEntity();
		bean.setId(request.getId());
		bean.setStatus(request.getStatus());
		return roleMapper.updateById(bean);
	}

	@Override
	public Integer changeDataScope(RoleRequest request) {
		if(Objects.isNull(request.getId()) || Objects.isNull(request.getDataScope())){
			throw new SoException("更新角色状态失败！");
		}
		if(request.getId().equals(1L)){
			throw new SoException("不能更新admin权限！");
		}
		RoleEntity bean = new RoleEntity();
		bean.setId(request.getId());
		bean.setDataScope(request.getDataScope());
		return roleMapper.updateById(bean);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer authorizationMenu(RoleRequest request) {
		if(Objects.isNull(request.getId()) || Objects.isNull(request.getMenuIds())){
			throw new SoException("授权菜单权限失败！");
		}
		if(request.getId().equals(1L)){
			throw new SoException("不能更新admin权限！");
		}
		LambdaQueryWrapper<RoleMenuEntity> queryWrapper = Wrappers.<RoleMenuEntity>lambdaQuery()
				.eq(RoleMenuEntity::getRoleId,request.getId());
		roleMenuMapper.delete(queryWrapper);
		int resultCount = 0;
		for(Long menuId : request.getMenuIds()){
			RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
			roleMenuEntity.setRoleId(request.getId());
			roleMenuEntity.setMenuId(menuId);
			resultCount = roleMenuMapper.insert(roleMenuEntity)+resultCount;
		}
		return resultCount;
	}

	@Override
	public List<RoleResponse> getList(RoleRequest request) {
		LambdaQueryWrapper<RoleEntity> queryWrapper = Wrappers.<RoleEntity>lambdaQuery()
				.eq(RoleEntity::getStatus,0)
				.orderByAsc(RoleEntity::getRoleSort);
		List<RoleEntity> roleEntities = roleMapper.selectList(queryWrapper);
		return BeanUtils.copyList(roleEntities,RoleResponse.class);
	}

	@Override
	public Set<String> getRolePermission(String userId) {
		List<RoleResponse> roleByUserIdList = this.getRoleByUserId(userId);
		Set<String> collect = roleByUserIdList.stream().map(RoleResponse::getRoleCode).collect(Collectors.toSet());
		return collect;
	}

	@Override
	public List<RoleResponse> getRoleByUserId(String userId) {
		return roleMapper.getRoleByUserId(userId);
	}

	@Override
	public void setLoginUserRoleService(LoginUser loginUser) {
		List<RoleResponse> roleByUserIdList = this.getRoleByUserId(loginUser.getId());
		if(CollectionUtils.isNotEmpty(roleByUserIdList)){
			Set<String> roles = roleByUserIdList.stream().map(RoleResponse::getRoleCode).collect(Collectors.toSet());
			loginUser.setRoles(roles);
			// todo 默认取第一条，后面在考虑多角色多数据权限情况
			loginUser.setDataScopeEnum(DataScopeEnum.getDataScope(roleByUserIdList.get(0).getDataScope()));
		}
	}

	/**
	 * 校验角色是否可以删除
	 * @param id
	 */
	@Override
	public void checkRoleIsDelete(Long id) {
		LambdaQueryWrapper<UserRoleEntity> queryWrapper = Wrappers.<UserRoleEntity>lambdaQuery()
				.eq(UserRoleEntity::getRoleId,id);
		Long userCount = userRoleMapper.selectCount(queryWrapper);
		if(userCount>0){
			throw new SoException(String.format("当前有%s名用户绑定此角色，请解绑后在删除！",userCount));
		}
	}

}

