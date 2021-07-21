package cn.xianyum.system.service;


import cn.xianyum.common.utils.DataResult;
import cn.xianyum.system.entity.po.UserEntity;

public interface UserTokenService {

    /**
     * 生成token
     * @param user
     * @return
     */
    DataResult createToken(UserEntity user);

    /**
     * 退出，修改token值
     */
    void logout();

    /**
     * 刷新redis中user数据
     */
    void refreshUser();

    /**
     * 获取当前登录user
     * @return
     */
    UserEntity getUser();

}
