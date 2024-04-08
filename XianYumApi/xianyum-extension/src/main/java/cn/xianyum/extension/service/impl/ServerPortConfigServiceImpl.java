package cn.xianyum.extension.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
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
import java.util.Objects;

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

	@Override
	public PageResponse<ServerPortConfigResponse> getPage(ServerPortConfigRequest request) {
		LambdaQueryWrapper<ServerPortConfigEntity> queryWrapper = Wrappers.<ServerPortConfigEntity>lambdaQuery();
		Page<ServerPortConfigEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		IPage<ServerPortConfigEntity> pageResult = serverPortConfigMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,ServerPortConfigResponse.class);

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
	    int resultCount = 0;
		for (Long id : ids){
			resultCount = serverPortConfigMapper.deleteById(id)+resultCount;
		}
		return resultCount;
	}

	@Override
	public List<ServerPortConfigEntity> selectByServerId(Long serverId) {
		LambdaQueryWrapper<ServerPortConfigEntity> queryWrapper = Wrappers.<ServerPortConfigEntity>lambdaQuery()
				.eq(ServerPortConfigEntity::getServerConfigId,serverId);
		return this.serverPortConfigMapper.selectList(queryWrapper);
	}
}

