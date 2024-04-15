package cn.xianyum.extension.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.extension.dao.ServerConfigMapper;
import cn.xianyum.extension.entity.po.ServerConfigEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.common.entity.base.PageResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.xianyum.extension.entity.po.ServerPortConfigEntity;
import cn.xianyum.extension.entity.request.ServerPortConfigRequest;
import cn.xianyum.extension.entity.response.ServerPortConfigResponse;
import cn.xianyum.extension.service.ServerPortConfigService;
import cn.xianyum.extension.dao.ServerPortConfigMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 主机端口维护(ServerPortConfig)service层实现
 *
 * @author makejava
 * @since 2024-04-02 22:27:45
 */
@Service
@Slf4j
public class ServerPortConfigServiceImpl implements ServerPortConfigService {

	@Autowired
	private ServerPortConfigMapper serverPortConfigMapper;

	@Autowired
	private ServerConfigMapper serverConfigMapper;

	@Override
	public PageResponse<ServerPortConfigResponse> getPage(ServerPortConfigRequest request) {
		LambdaQueryWrapper<ServerPortConfigEntity> queryWrapper = Wrappers.<ServerPortConfigEntity>lambdaQuery()
				.eq(StringUtil.isNotEmpty(request.getPort()),ServerPortConfigEntity::getPort,request.getPort())
				.eq(Objects.nonNull(request.getServerConfigId()),ServerPortConfigEntity::getServerConfigId,request.getServerConfigId())
				.orderByDesc(ServerPortConfigEntity::getCreateTime);
		Page<ServerPortConfigEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		IPage<ServerPortConfigEntity> pageResult = serverPortConfigMapper.selectPage(page,queryWrapper);
		PageResponse<ServerPortConfigResponse> serverPortConfigResponsePageResponse = PageResponse.of(pageResult, ServerPortConfigResponse.class);
		if(serverPortConfigResponsePageResponse.getTotal() > 0){
			List<ServerPortConfigResponse> serverPortConfigResponseList = serverPortConfigResponsePageResponse.getDataList();
			List<Long> serverConfigList = serverPortConfigResponseList.stream().map(item -> item.getServerConfigId()).collect(Collectors.toList());
			LambdaQueryWrapper<ServerConfigEntity> serverConfigEntityLambdaQueryWrapper = Wrappers.<ServerConfigEntity>lambdaQuery()
					.in(ServerConfigEntity::getId,serverConfigList);
			List<ServerConfigEntity> serverConfigEntityList = this.serverConfigMapper.selectList(serverConfigEntityLambdaQueryWrapper);;
			Map<Long, String> idToNameMap = serverConfigEntityList.stream()
					.collect(Collectors.toMap(ServerConfigEntity::getId, ServerConfigEntity::getServerName));
			for (ServerPortConfigResponse serverPortConfigResponse : serverPortConfigResponsePageResponse.getDataList()) {
				serverPortConfigResponse.setServerName(idToNameMap.get(serverPortConfigResponse.getServerConfigId()));
			}
		}
		return serverPortConfigResponsePageResponse;
	}


	@Override
	public ServerPortConfigResponse getById(Long id) {
		ServerPortConfigEntity result = serverPortConfigMapper.selectById(id);
		ServerPortConfigResponse response = BeanUtils.copy(result, ServerPortConfigResponse.class);
		return response;
	}


	@Override
	public Integer save(ServerPortConfigRequest request) {
		ServerPortConfigEntity bean = BeanUtils.copy(request,ServerPortConfigEntity.class);
		return serverPortConfigMapper.insert(bean);
	}


	@Override
	public Integer update(ServerPortConfigRequest request) {
		if(Objects.isNull(request.getId())){
			throw new SoException("id不能为空");
		}
		ServerPortConfigEntity bean = BeanUtils.copy(request,ServerPortConfigEntity.class);
		return serverPortConfigMapper.updateById(bean);
	}


	@Override
	public Integer deleteById(Long[] ids) {
		for (Long id : ids){
			serverPortConfigMapper.deleteById(id);
		}
		return ids.length;
	}

	@Override
	public List<ServerPortConfigEntity> selectByServerId(Long serverId) {
		LambdaQueryWrapper<ServerPortConfigEntity> queryWrapper = Wrappers.<ServerPortConfigEntity>lambdaQuery()
				.eq(ServerPortConfigEntity::getServerConfigId,serverId);
		return this.serverPortConfigMapper.selectList(queryWrapper);
	}
}

