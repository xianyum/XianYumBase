package cn.xianyum.system.service;


import cn.xianyum.system.entity.response.UserThirdRelationResponse;

import java.util.List;

/**
 * (UserThirdRelation)service层
 *
 * @author zhangwei
 * @since 2024-03-05 15:03:57
 */
public interface UserThirdRelationService{

    Integer unbind(String id);

    List<UserThirdRelationResponse> getCurrentUserThirdRelation();
}
