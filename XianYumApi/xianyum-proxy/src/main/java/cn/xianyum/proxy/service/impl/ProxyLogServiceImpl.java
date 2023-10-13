package cn.xianyum.proxy.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.proxy.dao.ProxyLogMapper;
import cn.xianyum.proxy.entity.po.ProxyLogEntity;
import cn.xianyum.proxy.entity.request.ProxyLogRequest;
import cn.xianyum.proxy.entity.response.ProxyLogResponse;
import cn.xianyum.proxy.service.ProxyLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
@Slf4j
public class ProxyLogServiceImpl implements ProxyLogService {

	@Autowired
	private ProxyLogMapper proxyLogMapper;

	@Override
	public IPage<ProxyLogResponse> getPage(ProxyLogRequest request) {

		Page<ProxyLogEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<ProxyLogEntity> queryWrapper = new QueryWrapper<ProxyLogEntity>();
		IPage<ProxyLogEntity> pageResult = proxyLogMapper.selectPage(page,queryWrapper);
		IPage<ProxyLogResponse> responseIPage = new Page<>();
		responseIPage.setTotal(pageResult.getTotal());
		responseIPage.setRecords(BeanUtils.copyList(pageResult.getRecords(),ProxyLogResponse.class));
		return responseIPage;
	}

	@Override
	public ProxyLogResponse getById(Long id) {
		ProxyLogEntity result = proxyLogMapper.selectById(id);
		ProxyLogResponse response = BeanUtils.copy(result, ProxyLogResponse.class);
		return response;

	}

	@Override
	public Integer save(ProxyLogRequest request) {
		ProxyLogEntity bean = BeanUtils.copy(request,ProxyLogEntity.class);
		return proxyLogMapper.insert(bean);

	}

	@Override
	public Integer update(ProxyLogRequest request) {
		ProxyLogEntity bean = BeanUtils.copy(request,ProxyLogEntity.class);
		return proxyLogMapper.updateById(bean);

	}

	@Override
	public void deleteById(Long[] ids) {
		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (Long id : ids){
			proxyLogMapper.deleteById(id);
		}
	}

}
