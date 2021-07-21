package cn.xianyum.proxy.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.proxy.dao.ProxyDetailsMapper;
import cn.xianyum.proxy.entity.po.ProxyDetailsEntity;
import cn.xianyum.proxy.entity.request.ProxyDetailsRequest;
import cn.xianyum.proxy.entity.response.ProxyDetailsResponse;
import cn.xianyum.proxy.metrics.MetricsCollector;
import cn.xianyum.proxy.service.ProxyDetailsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProxyDetailsServiceImpl implements ProxyDetailsService {

	@Autowired
	private ProxyDetailsMapper proxyDetailsMapper;

	@Override
	public IPage<ProxyDetailsResponse> getPage(ProxyDetailsRequest request) {

		IPage<ProxyDetailsResponse> responseIPage = new Page<>();
		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			return responseIPage;
		}
		Page<ProxyDetailsEntity> page = new Page<>(request.getPageNum(),request.getPageSize());

		List<ProxyDetailsResponse> list = proxyDetailsMapper.getPage(request,page);
		for(ProxyDetailsResponse item:list){
			MetricsCollector collector = MetricsCollector.getCollector(item.getInetPort());
			if(collector != null){
				item.setConnectCount(collector.getChannels().get());
				item.setWriteBytesStr(ByteUtils.byteFormat(collector.getWroteBytes().get(),true));
				item.setReadBytesStr(ByteUtils.byteFormat(collector.getReadBytes().get(),true));
			}
		}
		responseIPage.setTotal(page.getTotal());
		responseIPage.setRecords(list);
		return responseIPage;
	}

	@Override
	public ProxyDetailsResponse getById(ProxyDetailsRequest request) {

		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		ProxyDetailsEntity result = proxyDetailsMapper.selectById(request.getId());
		ProxyDetailsResponse response = BeanUtils.copy(result, ProxyDetailsResponse.class);
		return response;

	}

	@Override
	public Integer save(ProxyDetailsRequest request) {

		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}
		if(null == request.getInetPort()){
			// 默认数据库最大端口+1，推荐使用这种方式
			QueryWrapper<ProxyDetailsEntity> queryWrapper
					= new QueryWrapper<ProxyDetailsEntity>()
					.notIn("inet_port",9201,9203)
					.orderByDesc("inet_port")
					.last("LIMIT 1");
			ProxyDetailsEntity proxyDetailsEntity = proxyDetailsMapper.selectOne(queryWrapper);
			if(proxyDetailsEntity != null){
				request.setInetPort(proxyDetailsEntity.getInetPort()+1);
			}else{
				throw new SoException("首次添加请填写端口，后续不填写会取数据库最大端口+1");
			}
		}

		QueryWrapper<ProxyDetailsEntity> queryWrapper
				= new QueryWrapper<ProxyDetailsEntity>().eq("inet_port",request.getInetPort());
		Integer portCount = proxyDetailsMapper.selectCount(queryWrapper);
		if(portCount > 0){
			throw new SoException("公网端口不能重复！");
		}

		ProxyDetailsEntity bean = BeanUtils.copy(request,ProxyDetailsEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		bean.setCreateTime(new Date());
		return proxyDetailsMapper.insert(bean);

	}

	@Override
	public Integer update(ProxyDetailsRequest request) {

		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}

		if(null == request.getInetPort()){
			throw new SoException("公网端口不能为空");
		}
		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}

		QueryWrapper<ProxyDetailsEntity> queryWrapper
				= new QueryWrapper<ProxyDetailsEntity>()
				.eq("inet_port",request.getInetPort())
				.ne("id",request.getId());
		Integer portCount = proxyDetailsMapper.selectCount(queryWrapper);
		if(portCount > 0){
			throw new SoException("公网端口不能重复！");
		}
		ProxyDetailsEntity bean = BeanUtils.copy(request,ProxyDetailsEntity.class);
		return proxyDetailsMapper.updateById(bean);

	}

	@Override
	public void deleteById(String[] ids) {

		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			proxyDetailsMapper.deleteById(id);
		}
	}

	@Override
	public List<Integer> getClientInetPorts(String clientKey) {
		QueryWrapper<ProxyDetailsEntity> queryWrapper
				= new QueryWrapper<ProxyDetailsEntity>().eq("proxy_id",clientKey);
		List<ProxyDetailsEntity> proxyDetailsEntities = proxyDetailsMapper.selectList(queryWrapper);
		if(proxyDetailsEntities != null && proxyDetailsEntities.size() > 0){
			List<Integer> collect = proxyDetailsEntities.stream().map(p -> p.getInetPort()).collect(Collectors.toList());
			return collect;
		}
		return null;
	}

	@Override
	public List<Integer> getUserPorts() {
		QueryWrapper<ProxyDetailsEntity> queryWrapper
				= new QueryWrapper<ProxyDetailsEntity>();
		List<ProxyDetailsEntity> proxyDetailsEntities = proxyDetailsMapper.selectList(queryWrapper);
		if(proxyDetailsEntities != null && proxyDetailsEntities.size() > 0){
			List<Integer> collect = proxyDetailsEntities.stream().map(p -> p.getInetPort()).collect(Collectors.toList());
			return collect;
		}
		return null;
	}

	@Override
	public String getLanInfo(Integer port) {
		QueryWrapper<ProxyDetailsEntity> queryWrapper
				= new QueryWrapper<ProxyDetailsEntity>().eq("inet_port",port);
		ProxyDetailsEntity proxyDetailsEntity = proxyDetailsMapper.selectOne(queryWrapper);
		if(proxyDetailsEntity == null){
			return null;
		}
		return proxyDetailsEntity.getLan();
	}

}
