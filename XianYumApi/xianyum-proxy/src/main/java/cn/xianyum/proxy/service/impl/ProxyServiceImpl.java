package cn.xianyum.proxy.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.proxy.common.utils.PushProxyUtils;
import cn.xianyum.proxy.container.ProxyChannelManager;
import cn.xianyum.proxy.container.ProxyServerContainer;
import cn.xianyum.proxy.dao.ProxyDetailsMapper;
import cn.xianyum.proxy.dao.ProxyMapper;
import cn.xianyum.proxy.entity.po.ProxyDetailsEntity;
import cn.xianyum.proxy.entity.po.ProxyEntity;
import cn.xianyum.proxy.entity.po.ProxySystemConstantEntity;
import cn.xianyum.proxy.entity.request.ProxyRequest;
import cn.xianyum.proxy.entity.response.ProxyResponse;
import cn.xianyum.proxy.handlers.ProxyChangedListener;
import cn.xianyum.proxy.service.ProxyService;
import cn.xianyum.proxy.service.ProxySystemConstantService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProxyServiceImpl implements ProxyService {

	@Autowired
	private ProxyMapper proxyMapper;

	@Autowired
	private ProxyDetailsMapper proxyDetailsMapper;

	@Autowired
	private PushProxyUtils pushProxyUtils;

	@Autowired
	private EmailUtils emailUtils;

	@Autowired
	private ProxySystemConstantService systemConstantService;


	@Override
	public IPage<ProxyResponse> getPage(ProxyRequest request) {

		IPage<ProxyResponse> responseIPage = new Page<>();
		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			return responseIPage;
		}
		Page<ProxyEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		QueryWrapper<ProxyEntity> queryWrapper = new QueryWrapper<ProxyEntity>()
				.like(StringUtil.isNotEmpty(request.getId()),"id",request.getId())
				.like(StringUtil.isNotEmpty(request.getName()),"name",request.getName())
				.orderByDesc("create_time")
				.orderByDesc("id");
		IPage<ProxyEntity> pageResult = proxyMapper.selectPage(page,queryWrapper);

		responseIPage.setTotal(pageResult.getTotal());
		List<ProxyResponse> proxyResponses = BeanUtils.copyList(pageResult.getRecords(), ProxyResponse.class);
		responseIPage.setRecords(proxyResponses);
		for(ProxyResponse item : proxyResponses){
			Channel channel = ProxyChannelManager.getCmdChannel(item.getId());
			if (channel != null) {
				item.setStatus(1);// online
			} else {
				item.setStatus(0);// offline
			}
		}
		return responseIPage;
	}

	@Override
	public ProxyResponse getById(ProxyRequest request) {
		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}
		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		ProxyEntity result = proxyMapper.selectById(request.getId());
		ProxyResponse response = BeanUtils.copy(result, ProxyResponse.class);
		return response;

	}

	@Override
	public Integer save(ProxyRequest request) {

		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}

		ProxyEntity bean = BeanUtils.copy(request,ProxyEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		bean.setCreateTime(new Date());
		bean.setLoginCount(0);
		return proxyMapper.insert(bean);

	}

	@Override
	public Integer update(ProxyRequest request) {

		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}
		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		ProxyEntity bean = new ProxyEntity();
		bean.setId(request.getId());
		bean.setName(request.getName());
		bean.setNotifyEmail(request.getNotifyEmail());
		bean.setNotify(request.getNotify());
		return proxyMapper.updateById(bean);

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
			QueryWrapper<ProxyDetailsEntity> queryWrapper = new QueryWrapper<ProxyDetailsEntity>()
					.eq("proxy_id",id);
			Integer count = proxyDetailsMapper.selectCount(queryWrapper);
			if(count > 0){
				throw new SoException("请先删除该客户端下的代理配置！");
			}
			proxyMapper.deleteById(id);
		}
	}

	@Override
	public Set<String> getAllClientKey() {
		QueryWrapper<ProxyEntity> queryWrapper = new QueryWrapper<ProxyEntity>();
		List<ProxyEntity> proxyEntities = proxyMapper.selectList(queryWrapper);

		if(proxyEntities != null && proxyEntities.size() > 0){
			Set<String> collect = proxyEntities.stream().map(p -> p.getId()).collect(Collectors.toSet());
			return collect;
		}
		return null;
	}

	@Override
	public void onlineNotify(String clientKey) {

		ProxyEntity proxyEntity = proxyMapper.selectById(clientKey);

		if(proxyEntity != null){
			DateTime now = new DateTime();
			ProxyEntity updateBean = new ProxyEntity();
			updateBean.setId(clientKey);
			updateBean.setLoginCount(proxyEntity.getLoginCount()+1);
			updateBean.setLoginTime(now.toDate());
			proxyMapper.updateById(updateBean);

			Map<String,Object> content = new LinkedHashMap<>();
			content.put("客户端名称：",proxyEntity.getName());
			content.put("登录次数：",proxyEntity.getLoginCount()+1);
			content.put("登录时间：",now.toString(DateUtils.DATE_TIME_PATTERN));
			pushProxyUtils.push(content,"xianyu客户端上线通知");



			// 发送邮件通知客户端
			if(null != proxyEntity.getNotify() && 1 == proxyEntity.getNotify()
					&& StringUtil.isNotEmpty(proxyEntity.getNotifyEmail())){
				this.sendProxyEmail(proxyEntity);
			}

		}
	}

	@Override
	public void offlineNotify(String clientKey) {

		ProxyEntity proxyEntity = proxyMapper.selectById(clientKey);

		if(proxyEntity != null){
			DateTime now = new DateTime();
			Map<String,Object> content = new LinkedHashMap<>();
			content.put("客户端名称：",proxyEntity.getName());
			content.put("在线时长：",DateUtils.getDatePoor(now.toDate(),proxyEntity.getLoginTime()));
			content.put("下线时间：",now.toString(DateUtils.DATE_TIME_PATTERN));
			pushProxyUtils.push(content,"xianyu客户端下线通知");
		}
	}

	@Override
	public List<ProxyResponse> getList(ProxyRequest request) {

		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			return null;
		}
		QueryWrapper<ProxyEntity> queryWrapper = new QueryWrapper<ProxyEntity>();
		List<ProxyEntity> proxyEntities = proxyMapper.selectList(queryWrapper);
		List<ProxyResponse> proxyResponses = BeanUtils.copyList(proxyEntities, ProxyResponse.class);
		return proxyResponses;
	}

	@Override
	public void flushProxy() {
		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}
		ProxyChangedListener proxyChangedListener = new ProxyChannelManager();
		ProxyChangedListener proxyChangedListener1 = new ProxyServerContainer();
		proxyChangedListener.onChanged();
		proxyChangedListener1.onChanged();
	}

	@Override
	public void sendProxyEmail(ProxyEntity proxyEntity) {

		Context context = new Context();
		context.setVariable("name", proxyEntity.getName());
		QueryWrapper<ProxyDetailsEntity> queryWrapper
				= new QueryWrapper<ProxyDetailsEntity>().eq("proxy_id",proxyEntity.getId());
		List<ProxyDetailsEntity> proxyDetailsEntities = proxyDetailsMapper.selectList(queryWrapper);

		if(proxyDetailsEntities != null && proxyDetailsEntities.size() >0){
			StringBuilder remoteAddr = new StringBuilder();
			for(ProxyDetailsEntity item : proxyDetailsEntities){
				remoteAddr.append("mstsc.xianyum.cn:");
				remoteAddr.append(item.getInetPort());
				remoteAddr.append("(");
				remoteAddr.append(item.getLan());
				remoteAddr.append(")");
				remoteAddr.append(",");
			}
			remoteAddr.deleteCharAt(remoteAddr.length()-1);
			context.setVariable("remoteAddr", remoteAddr.toString());
		}else{
			context.setVariable("remoteAddr", "暂未配置");
		}

		context.setVariable("loginCount", proxyEntity.getLoginCount()+"次");
		if(null != proxyEntity.getLoginTime()){
			context.setVariable("loginTime", DateUtils.format(proxyEntity.getLoginTime(),DateUtils.DATE_TIME_PATTERN));
		}else{
			context.setVariable("loginTime", DateTime.now().toString(DateUtils.DATE_TIME_PATTERN));
		}
		ProxySystemConstantEntity systemConstantEntity = systemConstantService.getByKey("xianyu_client");
		if(systemConstantEntity != null){
			JSONObject jsonObject = JSONObject.parseObject(systemConstantEntity.getConstantValue());
			context.setVariable("clientDownloadUrl", jsonObject.getString("downloadUrl"));
			context.setVariable("notice", jsonObject.getString("notice"));
		}else{
			context.setVariable("clientDownloadUrl", "http://xianyum.cn");
			context.setVariable("notice", "暂未有最新公告");
		}

		Channel channel = ProxyChannelManager.getCmdChannel(proxyEntity.getId());
		if(channel != null){
			context.setVariable("online", "在线");
		}else {
			context.setVariable("online", "离线");
		}
		emailUtils.sendHtmlEmail(proxyEntity.getNotifyEmail(),"xianyu客户端代理配置详情","clientHtml",context);

	}

	@Override
	public String sendEmail(ProxyRequest request) {

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("客户端授权码不能为空！");
		}

		ProxyEntity proxyEntity = proxyMapper.selectById(request.getId());
		if(proxyEntity == null){
			throw new SoException("客户端授权信息不存在！");
		}

		if(StringUtil.isEmpty(proxyEntity.getNotifyEmail())){
			throw new SoException("客户端尚未配置邮箱账号，请联系管理员配置！");
		}

		this.sendProxyEmail(proxyEntity);

		String result = "远程配置已经发到您的邮箱："+proxyEntity.getNotifyEmail()+"，请注意查收！";
		return result;
	}

	@Override
	public String downloadConfig(ProxyRequest request) {

		if(!"admin".equals(SecurityUtils.getLoginUser().getUsername())){
			throw new SoException("您无权进行操作！");
		}

		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("客户端授权码不能为空！");
		}

		ProxyEntity proxyEntity = proxyMapper.selectById(request.getId());
		if(proxyEntity == null){
			throw new SoException("客户端授权信息不存在！");
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("serverAddress","mstsc.xianyum.cn");
		jsonObject.put("serverPort","9101");
		jsonObject.put("userKey",proxyEntity.getId());
		jsonObject.put("sslEnable",true);
		jsonObject.put("sslJksPath","xianyu.jks");
		jsonObject.put("keyStorePassword","123456");
		jsonObject.put("autoStart",false);
		String configInfo = jsonObject.toString();
		return DesUtils.getEncryptString(configInfo);
	}

}
