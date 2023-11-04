package cn.xianyum.system.service.impl;

import cn.xianyum.common.config.XianYumConfig;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.DeleteTagEnum;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.enums.UserStatusEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.common.utils.SecretUtils;
import cn.xianyum.system.dao.RoleMapper;
import cn.xianyum.system.dao.ThirdUserMapper;
import cn.xianyum.system.dao.UserMapper;
import cn.xianyum.system.dao.UserRoleMapper;
import cn.xianyum.system.entity.po.*;
import cn.xianyum.system.entity.request.UpdatePasswordRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.entity.response.RoleResponse;
import cn.xianyum.system.entity.response.UserResponse;
import cn.xianyum.system.service.*;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ThirdUserMapper aliUserMapper;

    @Autowired
    private AliNetService aliNetService;

    @Autowired
    private QqNetService qqNetService;

    @Autowired
    private SystemConstantService systemConstantService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public PageResponse<UserResponse> getPage(UserRequest user) {
        Page<UserEntity> page = new Page<>(user.getPageNum(),user.getPageSize());
        //查询总记录数
        if(!SecurityUtils.isSupperAdminAuth()){
            user.setId(SecurityUtils.getLoginUser().getId());
        }else{
            user.setId(null);
        }
        List<UserResponse> userResponseList = userMapper.queryAll(user, page);
        return PageResponse.of(page.getTotal(),userResponseList,UserResponse.class,(response,item)->{
            response.setGroupRoleName(roleMapper.getRoleByUserId(item.getId()).stream().map(RoleResponse::getRoleName).collect(Collectors.joining(",")));
        });
    }

    @Override
    public UserEntity queryByUserName(String username) {
        UserEntity user = userMapper.selectOne(
                new QueryWrapper<UserEntity>().eq("username",username)
        );
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String[] userIds) {
        UserEntity userEntity = new UserEntity();
        userEntity.setDelTag(UserStatusEnum.BAN.getStatus());
        for(String id : userIds){
            userEntity.setId(id);
            userMapper.updateById(userEntity);
            // 删除用户的同时，解绑角色
            LambdaQueryWrapper<UserRoleEntity> queryWrapper = Wrappers.<UserRoleEntity>lambdaQuery()
                    .eq(UserRoleEntity::getUserId,id);
            userRoleMapper.delete(queryWrapper);
        }
    }

    @Override
    public UserResponse selectOneById(String id) {
        UserEntity userEntity = userMapper.selectById(id);
        UserResponse userResponse = BeanUtils.copy(userEntity, UserResponse.class);
        if(Objects.nonNull(userResponse)){
            List<RoleResponse> roleByUserId = roleMapper.getRoleByUserId(userEntity.getId());
            userResponse.setRoleIds(roleByUserId.stream().map(RoleResponse::getId).collect(Collectors.toList()));
        }
        return Objects.isNull(userResponse)?new UserResponse():userResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(UserRequest user) {
        List<UserEntity> repeatList = userMapper.getList(user);
        if(repeatList != null && repeatList.size() >0){
            throw new SoException("用户名或手机号已被使用！");
        }
        UserEntity userEntity = BeanUtils.copy(user, UserEntity.class);
        userEntity.setPassword(SecretUtils.encryptPassword(user.getPassword()));
        userEntity.setDelTag(DeleteTagEnum.Delete.getDeleteTag());
        if(userEntity.getStatus() == null){
            userEntity.setStatus(UserStatusEnum.ALLOW.getStatus());
        }
        String userId = UUIDUtils.UUIDReplace();
        userEntity.setId(userId);
        int count = userMapper.insert(userEntity);
        if(count > 0){
            this.changeUserRole(userId,user.getRoleIds());
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UserRequest user) {

        UserEntity userEntity = BeanUtils.copy(user, UserEntity.class);
        List<UserEntity> repeatList = userMapper.getList(user).stream().filter(p -> !user.getId().equals(p.getId())).collect(Collectors.toList());
        if(repeatList != null && repeatList.size() >0){
            throw new SoException("用户名或手机号已被使用！");
        }
        if(StringUtil.isNotEmpty(user.getPassword())){
            userEntity.setPassword(SecretUtils.encryptPassword(user.getPassword()));
        }
        int count = userMapper.updateById(userEntity);
        if(count > 0){
            this.changeUserRole(userEntity.getId(),user.getRoleIds());
        }
        return count;
    }

    @Override
    public boolean updatePassword(UpdatePasswordRequest info) {
        LoginUser userEntity = SecurityUtils.getLoginUser();
        String password = info.getPassword();
        if(!SecretUtils.matchesPassword(password,userEntity.getPassword())){
            return false;
        }
        UserEntity user = new UserEntity();
        user.setId(userEntity.getId());
        String secret = SecretUtils.encryptPassword(info.getNewPassword());
        user.setPassword(secret);
        int count = userMapper.updateById(user);
        if(count > 0){
            return true;
        }
        return false;
    }

    @Override
    public LoginUser getUserByQq(String authCode) {
        if(StringUtil.isBlank(authCode)){
            return null;
        }
        String accessToken = qqNetService.getAccessToken(authCode);
        QqUserEntity qqUserEntity = qqNetService.getUserId(accessToken);
        if(StringUtil.isNotBlank(qqUserEntity.getUserId())){
            LoginUser loginUser = new LoginUser();
            ThirdUserEntity aliUserEntity = aliUserMapper.selectOne(new QueryWrapper<ThirdUserEntity>().eq("qq_user_id",qqUserEntity.getUserId()));
            if(aliUserEntity == null ){
                loginUser.setId(qqUserEntity.getUserId());
                loginUser.setUsername(EmojiUtils.filterEmoji(qqUserEntity.getNickname()));
                loginUser.setStatus(UserStatusEnum.ALLOW.getStatus());
                if("女".equals(qqUserEntity.getGender())){
                    loginUser.setSex(1);
                }else{
                    loginUser.setSex(0);
                }
                // 现在qq返回的是http连接，实际https也是支持的，这里替换下
                if(StringUtil.isNotEmpty(qqUserEntity.getFigureurl_qq_2())){
                    loginUser.setAvatar(qqUserEntity.getFigureurl_qq_2().replace("http","https"));
                }else{
                    loginUser.setAvatar(qqUserEntity.getFigureurl_qq_1().replace("http","https"));
                }
            }else{
                UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .eq("id",aliUserEntity.getUserId())
                        .eq("del_tag",UserStatusEnum.ALLOW.getStatus()));
                loginUser = BeanUtils.copy(userEntity,LoginUser.class);
            }
            loginUser.setLoginType(LoginTypeEnum.QQ.getLoginType());
            return loginUser;
        }
        return null;
    }

    @Override
    public int updateCurrentUser(UserRequest user) {
        user.setId(SecurityUtils.getLoginUser().getId());
        int count = userMapper.updateUser(user);
        userTokenService.refreshUser();
        return count;
    }

    @Override
    public LoginUser getUserSelf() {
        LoginUser u = userTokenService.getUserSelf();
        if(null != u && StringUtil.isEmpty(u.getAvatar())){
            SystemConstantEntity systemConstantEntity = systemConstantService.getByKey("avatar_url");
            if(systemConstantEntity != null){
                u.setAvatar(systemConstantEntity.getConstantValue());
            }
        }
        return u;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            String upload = FileUtils.upload(XianYumConfig.getXianYumConfig().getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            String avatarUrl = XianYumConfig.getXianYumConfig().getAvatarUrl()+ upload;
            UserEntity userEntity = new UserEntity();
            userEntity.setId(SecurityUtils.getLoginUser().getId());
            userEntity.setAvatar(avatarUrl);
            userMapper.updateById(userEntity);
            userTokenService.refreshUser();
            return avatarUrl;
        }catch (Exception e){
            throw new SoException(e.getMessage());
        }
    }

    /**
     * 更新用户权限
     *
     * @param id
     * @param roleIds
     */
    @Override
    public void changeUserRole(String id, List<Long> roleIds) {
        LambdaQueryWrapper<UserRoleEntity> queryWrapper = Wrappers.<UserRoleEntity>lambdaQuery()
                .eq(UserRoleEntity::getUserId,id);
        userRoleMapper.delete(queryWrapper);
        for (Long roleId : roleIds){
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setRoleId(roleId);
            userRoleEntity.setUserId(id);
            userRoleMapper.insert(userRoleEntity);
        }
    }

    /**
     * 个人中心
     *
     * @return
     */
    @Override
    public UserResponse getUserProfile() {
        String userId = SecurityUtils.getLoginUser().getId();
        UserEntity userEntity = userMapper.selectById(userId);
        UserResponse useResponse = BeanUtils.copy(userEntity, UserResponse.class);
        if(null != useResponse){
            if(StringUtil.isEmpty(useResponse.getAvatar())){
                SystemConstantEntity systemConstantEntity = systemConstantService.getByKey("avatar_url");
                if(systemConstantEntity != null){
                    useResponse.setAvatar(systemConstantEntity.getConstantValue());
                }
            }
            String groupRoleName = roleMapper.getRoleByUserId(userId).stream().map(RoleResponse::getRoleName).collect(Collectors.joining(","));
            useResponse.setGroupRoleName(groupRoleName);
        }
        return useResponse;
    }

    @Override
    public int changeStatus(UserRequest request) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(request.getId());
        userEntity.setStatus(request.getStatus());
        return userMapper.updateById(userEntity);
    }

    @Override
    public LoginUser getUserByAli(String authCode) {
        if(StringUtil.isBlank(authCode)){
            return null;
        }
        String accessToken = aliNetService.getAccessToken(authCode);
        AlipayUserInfoShareResponse aLiUserInfo = aliNetService.getALiUserInfo(accessToken);
        if(aLiUserInfo.isSuccess()){
            LoginUser loginUser = new LoginUser();
            ThirdUserEntity aliUserEntity = aliUserMapper.selectOne(new QueryWrapper<ThirdUserEntity>().eq("ali_user_id",aLiUserInfo.getUserId()));
            //如果没有查到与系统用户关联的，自动生成一个用户信息
            if(aliUserEntity == null ){
                loginUser.setId(aLiUserInfo.getUserId());
                loginUser.setUsername(EmojiUtils.filterEmoji(aLiUserInfo.getNickName()));
                loginUser.setStatus(UserStatusEnum.ALLOW.getStatus());
                loginUser.setAvatar(aLiUserInfo.getAvatar());
            }else{
                UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .eq("id",aliUserEntity.getUserId())
                        .eq("del_tag",UserStatusEnum.ALLOW.getStatus()));
                loginUser = BeanUtils.copy(userEntity,LoginUser.class);
            }
            loginUser.setLoginType(LoginTypeEnum.ZHI_FU_BAO.getLoginType());
            return loginUser;
        }
        return null;
    }
}
