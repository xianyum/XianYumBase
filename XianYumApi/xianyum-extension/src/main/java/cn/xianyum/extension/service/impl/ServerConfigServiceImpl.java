package cn.xianyum.extension.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.IPUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.extension.entity.po.ServerPortConfigEntity;
import cn.xianyum.extension.service.ServerPortConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.common.entity.base.PageResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.xianyum.extension.entity.po.ServerConfigEntity;
import cn.xianyum.extension.entity.request.ServerConfigRequest;
import cn.xianyum.extension.entity.response.ServerConfigResponse;
import cn.xianyum.extension.service.ServerConfigService;
import cn.xianyum.extension.dao.ServerConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 主机维护(ServerConfig)service层实现
 *
 * @author zhangwei
 * @since 2024-04-02 22:27:34
 */
@Service
@Slf4j
public class ServerConfigServiceImpl implements ServerConfigService {

	@Autowired
	private ServerConfigMapper serverConfigMapper;

	@Autowired
	private ServerPortConfigService serverPortConfigService;

	@Override
	public PageResponse<ServerConfigResponse> getPage(ServerConfigRequest request) {
		LambdaQueryWrapper<ServerConfigEntity> queryWrapper = Wrappers.<ServerConfigEntity>lambdaQuery()
				.like(StringUtil.isNotEmpty(request.getServerName()),ServerConfigEntity::getServerName,request.getServerName())
				.eq(StringUtil.isNotEmpty(request.getTag()),ServerConfigEntity::getTag,request.getTag())
				.orderByDesc(ServerConfigEntity::getCreateTime);
		Page<ServerConfigEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		IPage<ServerConfigEntity> pageResult = serverConfigMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,ServerConfigResponse.class);
	}


	@Override
	public ServerConfigResponse getById(Long id) {
		ServerConfigEntity result = serverConfigMapper.selectById(id);
		ServerConfigResponse response = BeanUtils.copy(result, ServerConfigResponse.class);
		return response;
	}


	@Override
	public Integer save(ServerConfigRequest request) {
		ServerConfigEntity bean = BeanUtils.copy(request,ServerConfigEntity.class);
		if(StringUtil.isNotEmpty(bean.getServerPublicIp())){
			bean.setServerLocation(IPUtils.getIpInfo(bean.getServerPublicIp()));
		}
		return serverConfigMapper.insert(bean);
	}


	@Override
	public Integer update(ServerConfigRequest request) {
		if(Objects.isNull(request.getId())){
			throw new SoException("id不能为空");
		}
		ServerConfigEntity bean = BeanUtils.copy(request,ServerConfigEntity.class);
		if(StringUtil.isNotEmpty(bean.getServerPublicIp())){
			bean.setServerLocation(IPUtils.getIpInfo(bean.getServerPublicIp()));
		}
		return serverConfigMapper.updateById(bean);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer deleteById(Long[] ids) {
		List<Long> idsStream = Arrays.asList(ids);
		idsStream.forEach(item -> {
			// 校验是否有绑定的数据
			List<ServerPortConfigEntity> serverPortConfigEntityList = this.serverPortConfigService.selectByServerId(item);
			if(!CollectionUtils.isEmpty(serverPortConfigEntityList)){
				throw new SoException("该主机在端口配置里有应用，暂不能删除！");
			}
		});
		return idsStream.stream().mapToInt(id -> serverConfigMapper.deleteById(id)).sum();
	}

	@Override
	public List<ServerConfigResponse> getList(ServerConfigRequest request) {
		LambdaQueryWrapper<ServerConfigEntity> queryWrapper = Wrappers.<ServerConfigEntity>lambdaQuery()
				.orderByDesc(ServerConfigEntity::getCreateTime);
		List<ServerConfigEntity> serverConfigEntities = this.serverConfigMapper.selectList(queryWrapper);
		return BeanUtils.copyList(serverConfigEntities,ServerConfigResponse.class);
	}

	@Override
	public List<ServerConfigEntity> selectByIds(List<Long> serverConfigIdList) {
		LambdaQueryWrapper<ServerConfigEntity> queryWrapper = Wrappers.<ServerConfigEntity>lambdaQuery()
				.in(ServerConfigEntity::getId,serverConfigIdList);
		return this.serverConfigMapper.selectList(queryWrapper);
	}
}

