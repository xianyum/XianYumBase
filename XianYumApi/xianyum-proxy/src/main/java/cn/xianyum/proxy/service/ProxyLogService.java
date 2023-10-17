package cn.xianyum.proxy.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.proxy.entity.po.ProxyLogEntity;
import cn.xianyum.proxy.entity.request.ProxyLogRequest;
import cn.xianyum.proxy.entity.response.ProxyLogResponse;

public interface ProxyLogService {

	PageResponse getPage(ProxyLogRequest request);

	ProxyLogResponse getById(Long id);

	Integer save(ProxyLogRequest request);

	Integer update(ProxyLogEntity proxyLogEntity);

	void deleteById(Long[] ids);

    ProxyLogEntity getLatestProxyLog(String clientKey);

	ProxyLogEntity saveLog(String clientKey);

	void setIgnoreSaveFlag();
}
