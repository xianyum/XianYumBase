package cn.xianyum.system.service;


import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.ReturnT;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UpdatePasswordRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.entity.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface UserService{

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

    int updateCurrentUser(UserRequest user);

    LoginUser getUserSelf();

    String upload(MultipartFile file);

    /**
     * 更新用户权限
     * @param id
     * @param roleIds
     */
    void changeUserRole(String id, List<Long> roleIds);

    /**
     * 个人中心
     * @return
     */
    UserResponse getUserProfile();

    int changeStatus(UserRequest request);

    /**
     * 根据角色Id获取用户
     * @param roleId
     * @return
     */
    List<UserResponse> getByRoleId(String roleId);

    /**
     * 初始化默认用户
     * @param loginUser
     */
    void initDefaultUser(LoginUser loginUser);


    /**
     * 系统用户存储到redis中
     * @param isAsync true：异步执行  false: 同步执行
     * @return
     */
    int userToRedis(boolean isAsync);

    int userToRedis();

    /**
     * 绑定qq用户
     * @param authCode
     * @return
     */
    boolean bindQqUser(String authCode);

    /**
     * 绑定支付宝用户
     * @param authCode
     * @return
     */
    boolean binAlidUser(String authCode);
}
