package cn.xianyum.system.service;


import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UpdatePasswordRequest;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.entity.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserService{

    PageResponse<UserResponse> getPage(UserRequest user);

    UserEntity queryByUserName(String username);

    /**
     * 根据用户id查询用户缓存信息
     * @param userId
     * @return
     */
    LoginUser selectUserCacheById(String userId);

    void deleteById(String[] userIds);

    UserResponse selectOneById(String id);

    int save(UserRequest user);

    int update(UserRequest user);

    boolean updatePassword(UpdatePasswordRequest info);

    LoginUser getUserByAli(String authCode);

    LoginUser getUserByQq(UserLoginRequest request);

    int updateCurrentUser(UserRequest user);

    String upload(MultipartFile file);

    void changeUserRole(String id, List<Long> roleIds);

    UserResponse getUserProfile();

    int changeStatus(UserRequest request);

    List<UserResponse> getByRoleId(String roleId);

    void initDefaultUser(LoginUser loginUser);

    boolean bindQqUser(String authCode);

    boolean binAliUser(String authCode);
}
