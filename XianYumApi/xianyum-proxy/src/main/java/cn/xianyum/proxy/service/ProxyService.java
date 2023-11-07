package cn.xianyum.proxy.service;



import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.proxy.entity.po.ProxyEntity;
import cn.xianyum.proxy.entity.request.ProxyRequest;
import cn.xianyum.proxy.entity.response.ProxyResponse;
import java.util.List;
import java.util.Set;

public interface ProxyService {

	PageResponse<ProxyResponse> getPage(ProxyRequest request);

	/**
	 * 获取在线数量
	 * @return
	 */
	int getOnlineProxyCount();

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

    void sendProxyEmail(ProxyEntity proxyEntity, String email);

    String sendEmail(String id);

    String downloadConfig(String id);

	List<LoginUser> getProxyBindUser(String id);
}
