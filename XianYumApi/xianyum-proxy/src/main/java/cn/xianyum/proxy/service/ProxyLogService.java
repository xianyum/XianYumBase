package cn.xianyum.proxy.service;

import cn.xianyum.proxy.entity.po.ProxyLogEntity;
import cn.xianyum.proxy.entity.request.ProxyLogRequest;
import cn.xianyum.proxy.entity.response.ProxyLogResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ProxyLogService {

	IPage<ProxyLogResponse> getPage(ProxyLogRequest request);

	ProxyLogResponse getById(Long id);

	Integer save(ProxyLogRequest request);

	Integer update(ProxyLogEntity proxyLogEntity);

	void deleteById(Long[] ids);

    ProxyLogEntity getLatestProxyLog(String clientKey);

	ProxyLogEntity saveLog(String clientKey);

	void setIgnoreSaveFlag();
}
