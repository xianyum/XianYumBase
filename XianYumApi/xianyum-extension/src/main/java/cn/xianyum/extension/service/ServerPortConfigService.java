package cn.xianyum.extension.service;

import cn.xianyum.extension.entity.po.ServerPortConfigEntity;
import cn.xianyum.extension.entity.request.ServerPortConfigRequest;
import cn.xianyum.extension.entity.response.ServerPortConfigResponse;
import cn.xianyum.common.entity.base.PageResponse;

import java.util.List;

/**
 * 主机端口维护(ServerPortConfig)service层
 *
 * @author makejava
 * @since 2024-04-02 22:27:45
 */
public interface ServerPortConfigService{

	PageResponse<ServerPortConfigResponse> getPage(ServerPortConfigRequest request);

	ServerPortConfigResponse getById(Long id);

	Integer save(ServerPortConfigRequest request);

	Integer update(ServerPortConfigRequest request);

	Integer deleteById(Long[] ids);

    List<ServerPortConfigEntity> selectByServerId(Long serverId);
}
