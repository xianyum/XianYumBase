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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProxyDetailsServiceImpl implements ProxyDetailsService {

	@Autowired
	private ProxyDetailsMapper proxyDetailsMapper;

	@Autowired
	private RedisUtils redisUtils;

	@Value("${redis.proxy.proxy_details.lan_info}")
	private String proxyDetailsLanInfoRedisKey;


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
			long nowWroteBytes = collector.getWroteBytes().get();
			long nowReadBytes = collector.getReadBytes().get();
			item.setWriteBytes(nowWroteBytes+item.getWriteBytes());
			item.setReadBytes(nowReadBytes+item.getReadBytes());
			if(collector != null){
				item.setConnectCount(collector.getChannels().get());
				item.setWriteBytesStr(ByteUtils.byteFormat(item.getWriteBytes(),true));
				item.setReadBytesStr(ByteUtils.byteFormat(item.getReadBytes(),true));
			}
		}
		responseIPage.setTotal(page.getTotal());
		responseIPage.setRecords(list);
		return responseIPage;
	}

	@Override
	public ProxyDetailsResponse getById(String id) {

		SecurityUtils.allowAdminAuth();

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		ProxyDetailsEntity result = proxyDetailsMapper.selectById(id);
		ProxyDetailsResponse response = BeanUtils.copy(result, ProxyDetailsResponse.class);
		return response;

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer save(ProxyDetailsRequest request) {

		SecurityUtils.allowAdminAuth();

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
		Long portCount = proxyDetailsMapper.selectCount(queryWrapper);
		if(portCount > 0){
			throw new SoException("公网端口不能重复！");
		}

		ProxyDetailsEntity bean = BeanUtils.copy(request,ProxyDetailsEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		bean.setCreateTime(new Date());

		// 删除缓存,后续在重新缓存
		String redisKey = proxyDetailsLanInfoRedisKey.concat(request.getInetPort().toString());
		redisUtils.del(redisKey);

		return proxyDetailsMapper.insert(bean);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer update(ProxyDetailsRequest request) {

		SecurityUtils.allowAdminAuth();

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
		Long portCount = proxyDetailsMapper.selectCount(queryWrapper);
		if(portCount > 0){
			throw new SoException("公网端口不能重复！");
		}
		ProxyDetailsEntity bean = BeanUtils.copy(request,ProxyDetailsEntity.class);
		bean.setReadBytes(null);
		bean.setWriteBytes(null);

		// 删除缓存,后续在重新缓存
		String redisKey = proxyDetailsLanInfoRedisKey.concat(request.getInetPort().toString());
		redisUtils.del(redisKey);
		return proxyDetailsMapper.updateById(bean);

	}

	@Override
	public void deleteById(String[] ids) {

		SecurityUtils.allowAdminAuth();

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
		if(Objects.isNull(port)){
			return null;
		}
		String redisKey = proxyDetailsLanInfoRedisKey.concat(port.toString());
		if(redisUtils.hasKey(redisKey)){
			return redisUtils.getString(redisKey);
		}
		QueryWrapper<ProxyDetailsEntity> queryWrapper
				= new QueryWrapper<ProxyDetailsEntity>().eq("inet_port",port);
		ProxyDetailsEntity proxyDetailsEntity = proxyDetailsMapper.selectOne(queryWrapper);
		if(proxyDetailsEntity == null){
			return null;
		}
		String lanInfo = proxyDetailsEntity.getLan();
		redisUtils.setDay(redisKey,lanInfo,1);
		return lanInfo;
	}

}
