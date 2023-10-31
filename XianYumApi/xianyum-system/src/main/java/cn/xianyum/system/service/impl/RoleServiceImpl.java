package cn.xianyum.system.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

import java.util.Objects;

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
		return response;
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
			resultCount = roleMapper.deleteById(id)+resultCount;
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

}

