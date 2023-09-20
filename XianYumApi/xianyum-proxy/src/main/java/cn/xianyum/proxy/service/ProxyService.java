package cn.xianyum.proxy.service;



import cn.xianyum.proxy.entity.po.ProxyEntity;
import cn.xianyum.proxy.entity.request.ProxyRequest;
import cn.xianyum.proxy.entity.response.ProxyResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Set;

public interface ProxyService {

	IPage<ProxyResponse> getPage(ProxyRequest request);

	ProxyResponse getById(String id);

	Integer save(ProxyRequest request);

	Integer update(ProxyRequest request);

	void deleteById(String[] ids);

	/**
	 * 获取所有的clientKey
	 * @return
	 */
	Set<String> getAllClientKey();

	/**
	 * 客户端上线通知
	 * @param clientKey
	 */
	void onlineNotify(String clientKey);

	/**
	 * 客户端下线通知
	 * @param clientKey
	 */
	void offlineNotify(String clientKey);

    List<ProxyResponse> getList();

    void flushProxy();

    void sendProxyEmail(ProxyEntity proxyEntity);

    String sendEmail(String id);

    String downloadConfig(String id);

	/**
	 * 更新客户端信息
	 * @param request
	 */
	void updateClientInfo(ProxyRequest request);
}
