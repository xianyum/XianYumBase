package cn.xianyum.system.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.request.UserOnlineRequest;
import cn.xianyum.system.entity.response.UserOnlineResponse;



/**
 * @author zhangwei
 * @date 2021/1/28 22:03
 */
public interface UserOnlineService {

    PageResponse<UserOnlineResponse> queryPage(UserOnlineRequest request);

    void delete(String[] tokenIds);
}
