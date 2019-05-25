package com.base.service.iservice;

import com.base.common.utils.DataResult;
import com.base.entity.po.UserEntity;

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
}
