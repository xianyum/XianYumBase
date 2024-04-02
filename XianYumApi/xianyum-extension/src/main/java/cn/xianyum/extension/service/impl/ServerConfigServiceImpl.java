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
import cn.xianyum.extension.entity.po.ServerConfigEntity;
import cn.xianyum.extension.entity.request.ServerConfigRequest;
import cn.xianyum.extension.entity.response.ServerConfigResponse;
import cn.xianyum.extension.service.ServerConfigService;
import cn.xianyum.extension.dao.ServerConfigMapper;
import org.springframework.stereotype.Service;
import java.util.Objects;

/**
 * 主机维护(ServerConfig)service层实现
 *
 * @author makejava
 * @since 2024-04-02 22:27:34
 */
@Service
@Slf4j
public class ServerConfigServiceImpl implements ServerConfigService {

	@Autowired
	private ServerConfigMapper serverConfigMapper;

	@Override
	public PageResponse<ServerConfigResponse> getPage(ServerConfigRequest request) {
		LambdaQueryWrapper<ServerConfigEntity> queryWrapper = Wrappers.<ServerConfigEntity>lambdaQuery();
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
		return serverConfigMapper.insert(bean);
	}


	@Override
	public Integer update(ServerConfigRequest request) {
		if(Objects.isNull(request.getId())){
			throw new SoException("id不能为空");
		}
		ServerConfigEntity bean = BeanUtils.copy(request,ServerConfigEntity.class);
		return serverConfigMapper.updateById(bean);
	}


	@Override
	public Integer deleteById(Long[] ids) {
	    int resultCount = 0;
		for (Long id : ids){
			resultCount = serverConfigMapper.deleteById(id)+resultCount;
		}
		return resultCount;
	}
}

