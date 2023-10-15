package cn.xianyum.proxy.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.xianyum.proxy.container.ProxyChannelManager;
import cn.xianyum.proxy.container.ProxyServerContainer;
import cn.xianyum.proxy.dao.ProxyDetailsMapper;
import cn.xianyum.proxy.dao.ProxyMapper;
import cn.xianyum.proxy.entity.po.ProxyDetailsEntity;
import cn.xianyum.proxy.entity.po.ProxyEntity;
import cn.xianyum.proxy.entity.po.ProxyLogEntity;
import cn.xianyum.proxy.entity.request.ProxyRequest;
import cn.xianyum.proxy.entity.response.ProxyResponse;
import cn.xianyum.proxy.handlers.ProxyChangedListener;
import cn.xianyum.proxy.service.ProxyLogService;
import cn.xianyum.proxy.service.ProxyService;
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
	private MessageSender messageSender;

	@Autowired
	private ProxyLogService proxyLogService;

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
				.orderByDesc("login_count");
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

	/**
	 * 获取在线数量
	 *
	 * @return
	 */
	@Override
	public int getOnlineProxyCount() {
		int count = 0;
		QueryWrapper<ProxyEntity> queryWrapper = new QueryWrapper<ProxyEntity>();
		List<ProxyEntity> proxyEntities = proxyMapper.selectList(queryWrapper);
		for(ProxyEntity item : proxyEntities){
			Channel channel = ProxyChannelManager.getCmdChannel(item.getId());
			if (channel != null) {
				count++;
			}
		}
		return count;
	}

	@Override
	public ProxyResponse getById(String id) {
		SecurityUtils.allowAdminAuth();

		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		ProxyEntity result = proxyMapper.selectById(id);
		ProxyResponse response = BeanUtils.copy(result, ProxyResponse.class);
		return response;

	}

	@Override
	public Integer save(ProxyRequest request) {

		SecurityUtils.allowAdminAuth();

		ProxyEntity bean = BeanUtils.copy(request,ProxyEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		bean.setLoginCount(0);
		return proxyMapper.insert(bean);

	}

	@Override
	public Integer update(ProxyRequest request) {

		SecurityUtils.allowAdminAuth();

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

		SecurityUtils.allowAdminAuth();

		if(null == ids || ids.length == 0){
			throw new SoException("id不能为空");
		}
		for (String id : ids){
			QueryWrapper<ProxyDetailsEntity> queryWrapper = new QueryWrapper<ProxyDetailsEntity>()
					.eq("proxy_id",id);
			Long count = proxyDetailsMapper.selectCount(queryWrapper);
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
			proxyMapper.updateById(updateBean);

			ProxyLogEntity proxyLogEntity = proxyLogService.saveLog(clientKey);

			Map<String,Object> content = new LinkedHashMap<>();
			content.put("客户端名称：",proxyEntity.getName());
			if(StringUtil.isNotEmpty(proxyLogEntity.getMacAddress())){
				content.put("客户端mac：",proxyLogEntity.getMacAddress());
			}
			if(StringUtil.isNotEmpty(proxyLogEntity.getHostIp())){
				content.put("客户端ip：",proxyLogEntity.getHostIp());
			}
			content.put("服务器节点：",IPUtils.getHostName());
			content.put("登录次数：",proxyEntity.getLoginCount()+1);
			content.put("登录时间：",now.toString(DateUtils.DATE_TIME_PATTERN));
			MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
			messageSenderEntity.setTitle("xianyu客户端上线通知");
			messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
			messageSender.sendAsyncMessage(MessageCodeEnums.XIANYU_ONLINE_OFFLINE_NOTIFY.getMessageCode(),messageSenderEntity);

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

			ProxyLogEntity proxyLogEntity = proxyLogService.getLatestProxyLog(clientKey);
			DateTime now = new DateTime();
			Map<String,Object> content = new LinkedHashMap<>();
			content.put("客户端名称：",proxyEntity.getName());
			// onlineTime如果不为空，说明有可能获取的不是最新的一条数据，可能客户端登录的时候未上报登录数据
			if(Objects.nonNull(proxyLogEntity.getId()) && StringUtil.isEmpty(proxyLogEntity.getOnlineTime())){
				String onlineTime = DateUtils.getDatePoor(now.toDate(), proxyLogEntity.getCreateTime());
				content.put("在线时长：",onlineTime);
				proxyLogEntity.setOnlineTime(onlineTime);
				proxyLogService.update(proxyLogEntity);
			}
			content.put("下线时间：",now.toString(DateUtils.DATE_TIME_PATTERN));
			MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
			messageSenderEntity.setTitle("xianyu客户端下线通知");
			messageSenderEntity.setMessageContents(MessageUtils.mapConvertMessageContentEntity(content));
			messageSender.sendAsyncMessage(MessageCodeEnums.XIANYU_ONLINE_OFFLINE_NOTIFY.getMessageCode(),messageSenderEntity);
		}
	}

	@Override
	public List<ProxyResponse> getList() {

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

		String serverAddress = "";
		String systemConstantJson = SystemConstantUtils.getValueByKey("xianyu_client");
		if(StringUtil.isNotEmpty(systemConstantJson)){
			JSONObject jsonObject = JSONObject.parseObject(systemConstantJson);
			context.setVariable("clientDownloadUrl", jsonObject.getString("downloadUrl"));
			context.setVariable("notice", jsonObject.getString("notice"));
			serverAddress = jsonObject.getString("serverAddress");
		}else{
			context.setVariable("clientDownloadUrl", "http://xianyum.cn");
			context.setVariable("notice", "暂未有最新公告");
		}

		if(proxyDetailsEntities != null && proxyDetailsEntities.size() >0){
			StringBuilder remoteAddr = new StringBuilder();
			for(ProxyDetailsEntity item : proxyDetailsEntities){
				remoteAddr.append(serverAddress);
				remoteAddr.append(":");
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

		ProxyLogEntity proxyLogEntity = proxyLogService.getLatestProxyLog(proxyEntity.getId());
		context.setVariable("loginCount", proxyEntity.getLoginCount()+"次");
		if(null != proxyLogEntity.getCreateTime()){
			context.setVariable("loginTime", DateUtils.format(proxyLogEntity.getCreateTime(),DateUtils.DATE_TIME_PATTERN));
		}else{
			context.setVariable("loginTime", DateTime.now().toString(DateUtils.DATE_TIME_PATTERN));
		}


		Channel channel = ProxyChannelManager.getCmdChannel(proxyEntity.getId());
		if(channel != null){
			context.setVariable("online", "在线");
		}else {
			context.setVariable("online", "离线");
		}
		context.setVariable("macAddress", proxyLogEntity.getMacAddress());
		context.setVariable("hostIp", proxyLogEntity.getHostIp());
		MessageSenderEntity messageSenderEntity = new MessageSenderEntity();
		messageSenderEntity.setTitle("xianyu客户端代理配置详情");
		messageSenderEntity.setEmailHtmlPath("clientHtml");
		messageSenderEntity.setEmailToUser(proxyEntity.getNotifyEmail());
		messageSender.sendAsyncEmailTemplateMessage(MessageCodeEnums.XIANYU_CONFIG_DETAIL_NOTIFY.getMessageCode(),messageSenderEntity,context);
	}

	@Override
	public String sendEmail(String id) {


		ProxyEntity proxyEntity = proxyMapper.selectById(id);
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
	public String downloadConfig(String id) {

		SecurityUtils.allowAdminAuth();

		ProxyEntity proxyEntity = proxyMapper.selectById(id);
		if(proxyEntity == null){
			throw new SoException("客户端授权信息不存在！");
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userKey",proxyEntity.getId());
		jsonObject.put("sslEnable",true);
		jsonObject.put("sslJksPath","xianyu.jks");
		jsonObject.put("keyStorePassword","123456");
		jsonObject.put("autoStart",false);
		String systemConstantJson = SystemConstantUtils.getValueByKey("xianyu_client");
		if(StringUtil.isNotEmpty(systemConstantJson)){
			JSONObject systemConstantObject = JSONObject.parseObject(systemConstantJson);
			jsonObject.put("apiUrl",systemConstantObject.getString("apiUrl"));
			jsonObject.put("serverAddress",systemConstantObject.getString("serverAddress"));
			jsonObject.put("serverPort",systemConstantObject.getString("serverPort"));
		}
		String configInfo = jsonObject.toString();
		return DesUtils.getEncryptString(configInfo);
	}

}
