package cn.xianyum.proxy.service.impl;

import cn.xianyum.common.entity.base.BaseEntity;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.proxy.dao.ProxyLogMapper;
import cn.xianyum.proxy.entity.po.ProxyLogEntity;
import cn.xianyum.proxy.entity.request.ProxyLogRequest;
import cn.xianyum.proxy.entity.response.ProxyLogResponse;
import cn.xianyum.proxy.service.ProxyLogService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Objects;

@Service
@Slf4j
public class ProxyLogServiceImpl implements ProxyLogService {

	@Autowired
	private ProxyLogMapper proxyLogMapper;

	@Value("${redis.proxy.proxy_log.ignore_save}")
	private String ignoreSaveFlagRedisKey;

	@Value("${redis.proxy.proxy_log.log_data}")
	private String logDataRedisKey;

	@Autowired
	private RedisUtils redisUtils;

	@Override
	public PageResponse<ProxyLogResponse> getPage(ProxyLogRequest request) {
		Page<ProxyLogEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		IPage<ProxyLogEntity> pageResult = proxyLogMapper.getPage(request,page);
		return PageResponse.of(pageResult,ProxyLogResponse.class);
	}

	@Override
	public ProxyLogResponse getById(Long id) {
		ProxyLogEntity result = proxyLogMapper.selectById(id);
		ProxyLogResponse response = BeanUtils.copy(result, ProxyLogResponse.class);
		return response;

	}

	@Override
	public Integer save(ProxyLogRequest request) {
		String proxyId = request.getProxyId();
		if(StringUtil.isEmpty(proxyId)){
			throw new SoException("客户端未上传授权码");
		}
		String redisKey = logDataRedisKey+proxyId;
		redisUtils.set(redisKey, JSONObject.toJSONString(request),120);
		return 1;
	}


	@Override
	public Integer update(ProxyLogEntity proxyLogEntity) {
		return proxyLogMapper.updateById(proxyLogEntity);
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

	@Override
	public ProxyLogEntity getLatestProxyLog(String clientKey) {
		LambdaQueryWrapper<ProxyLogEntity> queryWrapper = Wrappers.<ProxyLogEntity>lambdaQuery()
				.eq(ProxyLogEntity::getProxyId, clientKey)
				.orderByDesc(BaseEntity::getCreateTime)
				.last("limit 1");
		ProxyLogEntity proxyLogEntity = proxyLogMapper.selectOne(queryWrapper);
		return Objects.nonNull(proxyLogEntity)?proxyLogEntity:new ProxyLogEntity();
	}

	@Override
	public ProxyLogEntity saveLog(String clientKey) {
		ProxyLogEntity proxyLogEntity = new ProxyLogEntity();
		if(!redisUtils.hasKey(ignoreSaveFlagRedisKey)){
			String redisKey = logDataRedisKey+clientKey;
			if(redisUtils.hasKey(redisKey)){
				String result = redisUtils.getString(redisKey);
				proxyLogEntity = JSONObject.parseObject(result,ProxyLogEntity.class);
			}else{
				proxyLogEntity.setProxyId(clientKey);
			}
			proxyLogMapper.insert(proxyLogEntity);
		}
		return getLatestProxyLog(clientKey);
	}

	@Override
	public void setIgnoreSaveFlag() {
		redisUtils.setMin(ignoreSaveFlagRedisKey,"1",8);
	}

}
