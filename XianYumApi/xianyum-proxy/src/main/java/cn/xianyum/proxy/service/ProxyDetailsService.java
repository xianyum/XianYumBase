package cn.xianyum.proxy.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.proxy.entity.request.ProxyDetailsRequest;
import cn.xianyum.proxy.entity.response.ProxyDetailsResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface ProxyDetailsService {

	PageResponse<ProxyDetailsResponse> getPage(ProxyDetailsRequest request);

	ProxyDetailsResponse getById(String id);

	Integer save(ProxyDetailsRequest request);

	Integer update(ProxyDetailsRequest request);

	void deleteById(String[] ids);

	/**
	 * 根据clientKey获取端口号
	 * @param clientKey
	 * @return
	 */
	List<Integer> getClientInetPorts(String clientKey);

	/**
	 * 获取所有的用户绑定代理端口号
	 * @return
	 */
	List<Integer> getUserPorts();

	/**
	 * 根据端口号获取lan
	 * @param port
	 * @return
	 */
	String getLanInfo(Integer port);
}
