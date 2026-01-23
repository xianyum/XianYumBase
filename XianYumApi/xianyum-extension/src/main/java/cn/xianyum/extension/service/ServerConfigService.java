package cn.xianyum.extension.service;

import cn.xianyum.extension.entity.po.ServerConfigEntity;
import cn.xianyum.extension.entity.request.ServerConfigRequest;
import cn.xianyum.extension.entity.response.ServerConfigResponse;
import cn.xianyum.common.entity.base.PageResponse;

import java.util.List;

/**
 * 主机维护(ServerConfig)service层
 *
 * @author zhangwei
 * @since 2024-04-02 22:27:34
 */
public interface ServerConfigService{

	PageResponse<ServerConfigResponse> getPage(ServerConfigRequest request);

	ServerConfigResponse getById(Long id);

	Integer save(ServerConfigRequest request);

	Integer update(ServerConfigRequest request);

	Integer deleteById(Long[] ids);

	List<ServerConfigResponse> getList(ServerConfigRequest request);

	List<ServerConfigEntity> selectByIds(List<Long> serverConfigIdList);
}
