package cn.xianyum.system.service;


import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UpdatePasswordRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.entity.response.UserResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface UserService extends IService<UserEntity> {

    PageResponse<UserResponse> getPage(UserRequest user);

    UserEntity queryByUserName(String username);

    void deleteById(String[] userIds);

    UserResponse selectOneById(String id);

    int save(UserRequest user);

    int update(UserRequest user);

    boolean updatePassword(UpdatePasswordRequest info);

    /**
     * 通过阿里authCode获取系统用户信息
     * @param authCode
     * @return
     */
    LoginUser getUserByAli(String authCode);
    /**
     * 通过QQ authCode获取系统用户信息
     * @param authCode
     * @return
     */
    LoginUser getUserByQq(String authCode);

    Set<String> getPermissions();

    int updateCurrentUser(UserRequest user);

    LoginUser getUserSelf();

    String upload(MultipartFile file);

    /**
     * 更新用户权限
     * @param id
     * @param roleIds
     */
    void changeUserRole(String id, List<Long> roleIds);
}
