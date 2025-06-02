package cn.xianyum.proxy.service.impl;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.message.entity.po.MessageSenderEntity;
import cn.xianyum.message.enums.MessageCodeEnums;
import cn.xianyum.message.infra.sender.MessageSender;
import cn.xianyum.message.infra.utils.MessageUtils;
import cn.xianyum.proxy.infra.container.ProxyChannelManager;
import cn.xianyum.proxy.infra.container.ProxyServerContainer;
import cn.xianyum.proxy.dao.ProxyDetailsMapper;
import cn.xianyum.proxy.dao.ProxyMapper;
import cn.xianyum.proxy.entity.po.ProxyDetailsEntity;
import cn.xianyum.proxy.entity.po.ProxyEntity;
import cn.xianyum.proxy.entity.po.ProxyLogEntity;
import cn.xianyum.proxy.entity.request.ProxyRequest;
import cn.xianyum.proxy.entity.response.ProxyResponse;
import cn.xianyum.proxy.infra.handlers.ProxyChangedListener;
import cn.xianyum.proxy.service.ProxyLogService;
import cn.xianyum.proxy.service.ProxyService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

	@Autowired
	private UserCacheHelper userCacheHelper;

	@Override
	public PageResponse<ProxyResponse> getPage(ProxyRequest request) {
		Page<ProxyEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
		LambdaQueryWrapper<ProxyEntity> queryWrapper = Wrappers.<ProxyEntity>lambdaQuery()
				.like(StringUtil.isNotEmpty(request.getId()),ProxyEntity::getId,request.getId())
				.like(StringUtil.isNotEmpty(request.getName()),ProxyEntity::getName,request.getName())
				.eq(!SecurityUtils.isSupperAdminAuth(),ProxyEntity::getBindUserId,SecurityUtils.getLoginUser().getId())
				.orderByDesc(ProxyEntity::getCreateTime,ProxyEntity::getLoginCount);
		IPage<ProxyEntity> pageResult = proxyMapper.selectPage(page,queryWrapper);
		return PageResponse.of(pageResult,ProxyResponse.class,(response,item)->{
			Channel channel = ProxyChannelManager.getCmdChannel(item.getId());
			if (channel != null) {
				response.setStatus(1);// online
			} else {
				response.setStatus(0);// offline
			}
			if(StringUtil.isNotEmpty(item.getBindUserId())){
				LoginUser userByIdFromRedis = userCacheHelper.getUserByIdFromRedis(item.getBindUserId());
				if(Objects.nonNull(userByIdFromRedis)){
					response.setBindEmail(userByIdFromRedis.getEmail());
					response.setBindUserName(userByIdFromRedis.getUsername());
					response.setBindUserNickName(userByIdFromRedis.getNickName());
				}
			}
		});
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
		if(StringUtil.isEmpty(id)){
			throw new SoException("id不能为空");
		}
		ProxyEntity result = proxyMapper.selectById(id);
		ProxyResponse response = BeanUtils.copy(result, ProxyResponse.class);
		return response;

	}

	@Override
	public Integer save(ProxyRequest request) {
		ProxyEntity bean = BeanUtils.copy(request,ProxyEntity.class);
		bean.setId(UUIDUtils.UUIDReplace());
		bean.setLoginCount(0);
		return proxyMapper.insert(bean);

	}

	@Override
	public Integer update(ProxyRequest request) {
		if(StringUtil.isEmpty(request.getId())){
			throw new SoException("id不能为空");
		}
		ProxyEntity bean = new ProxyEntity();
		bean.setId(request.getId());
		bean.setName(request.getName());
		bean.setNotify(request.getNotify());
		bean.setBindUserId(request.getBindUserId());
		return proxyMapper.updateById(bean);

	}

	@Override
	public void deleteById(String[] ids) {
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
			LoginUser userByIdFromRedis = userCacheHelper.getUserByIdFromRedis(proxyEntity.getBindUserId());
			if(null != proxyEntity.getNotify() && 1 == proxyEntity.getNotify()
					&& Objects.nonNull(userByIdFromRedis) && StringUtil.isNotEmpty(userByIdFromRedis.getEmail())){
				this.sendProxyEmail(proxyEntity,userByIdFromRedis.getEmail());
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
	public void sendProxyEmail(ProxyEntity proxyEntity, String email) {

		Context context = new Context();
		context.setVariable("name", proxyEntity.getName());
		QueryWrapper<ProxyDetailsEntity> queryWrapper
				= new QueryWrapper<ProxyDetailsEntity>().eq("proxy_id",proxyEntity.getId());
		List<ProxyDetailsEntity> proxyDetailsEntities = proxyDetailsMapper.selectList(queryWrapper);

		String serverAddress = "";
		JSONObject jsonObject = SystemConstantUtils.getValueObjectByKey(SystemConstantKeyEnum.XIAN_YU_CLIENT);
		context.setVariable("clientDownloadUrl", jsonObject.getString("downloadUrl"));
		context.setVariable("notice", jsonObject.getString("notice"));
		serverAddress = jsonObject.getString("serverAddress");

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
		messageSenderEntity.setEmailToUser(email);
		messageSender.sendAsyncEmailTemplateMessage(MessageCodeEnums.XIANYU_CONFIG_DETAIL_NOTIFY.getMessageCode(),messageSenderEntity,context);
	}

	@Override
	public String sendEmail(String id) {

		ProxyEntity proxyEntity = proxyMapper.selectById(id);
		if(proxyEntity == null){
			throw new SoException("客户端授权信息不存在！");
		}

		LoginUser userByIdFromRedis = userCacheHelper.getUserByIdFromRedis(proxyEntity.getBindUserId());
		if(Objects.isNull(userByIdFromRedis)){
			throw new SoException("客户端暂未绑定账户，请联系管理员配置！");
		}

		if(StringUtil.isEmpty(userByIdFromRedis.getEmail())){
			throw new SoException("客户端绑定的账号尚未配置邮箱，请联系管理员配置！");
		}
		this.sendProxyEmail(proxyEntity, userByIdFromRedis.getEmail());
		String result = "远程配置已经发到您的邮箱："+userByIdFromRedis.getEmail()+"，请注意查收！";
		return result;
	}

	@Override
	public String downloadConfig(String id) {
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
		JSONObject systemConstantObject = SystemConstantUtils.getValueObjectByKey(SystemConstantKeyEnum.XIAN_YU_CLIENT);
		jsonObject.put("apiUrl",systemConstantObject.getString("apiUrl"));
		String configInfo = jsonObject.toString();
		return DesUtils.getEncryptString(configInfo);
	}

	@Override
	public List<LoginUser> getProxyBindUser(String id) {
		List<LoginUser> loginUserList = new ArrayList<>();
		LambdaQueryWrapper<ProxyEntity> queryWrapper = Wrappers.<ProxyEntity>lambdaQuery()
				.ne(StringUtil.isNotEmpty(id),ProxyEntity::getId,id)
				.isNotNull(ProxyEntity::getBindUserId);
		List<ProxyEntity> proxyEntities = proxyMapper.selectList(queryWrapper);
		if(Objects.nonNull(proxyEntities)){
			List<LoginUser> userListFromRedis = userCacheHelper.getUserListFromRedis();
			List<String> bindUserList = proxyEntities.stream().map(ProxyEntity::getBindUserId).collect(Collectors.toList());
			for(LoginUser item : userListFromRedis){
				if(!bindUserList.contains(item.getId())){
					LoginUser loginUser = new LoginUser();
					loginUser.setId(item.getId());
					loginUser.setNickName(item.getNickName());
					loginUser.setUsername(item.getUsername());
					loginUserList.add(loginUser);
				}
			}
		}
		return loginUserList;
	}

	@Override
	public ProxyResponse getCurrentProxy() {
		String userId = SecurityUtils.getLoginUser().getId();
		LambdaQueryWrapper<ProxyEntity> queryWrapper = Wrappers.<ProxyEntity>lambdaQuery()
				.eq(ProxyEntity::getBindUserId,userId);
		ProxyEntity proxy = proxyMapper.selectOne(queryWrapper);
		if(Objects.isNull(proxy)){
			throw new SoException("该用户未绑定远程客户端！");
		}
		ProxyResponse proxyResponse = BeanUtils.copy(proxy, ProxyResponse.class);
		if(StringUtil.isNotEmpty(proxyResponse.getBindUserId())){
			LoginUser userByIdFromRedis = userCacheHelper.getUserByIdFromRedis(proxyResponse.getBindUserId());
			if(Objects.nonNull(userByIdFromRedis)){
				proxyResponse.setBindEmail(userByIdFromRedis.getEmail());
				proxyResponse.setBindUserName(userByIdFromRedis.getUsername());
			}
		}
		Channel channel = ProxyChannelManager.getCmdChannel(proxy.getId());
		if (channel != null) {
			proxyResponse.setStatus(1);// online
		} else {
			proxyResponse.setStatus(0);// offline
		}
		return proxyResponse;
	}

}
