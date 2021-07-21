package cn.xianyum.system.service;

import cn.xianyum.system.entity.po.UserOnlineEntity;
import cn.xianyum.system.entity.request.UserOnlineRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;


/**
 * @author zhangwei
 * @date 2021/1/28 22:03
 */
public interface UserOnlineService {

    IPage<UserOnlineEntity> queryPage(UserOnlineRequest request);

    void delete(String[] tokenIds);
}
