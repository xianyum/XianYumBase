package cn.xianyum.system.service.impl;

import cn.xianyum.common.config.XianYumConfig;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.DataScopeEnum;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.enums.YesOrNoEnum;
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
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ThirdUserMapper thirdUserMapper;

    @Autowired
    private AliNetService aliNetService;

    @Autowired
    private QqNetService qqNetService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ThreadPoolTaskExecutor xianYumTaskExecutor;


    @Value("${redis.user.data}")
    private String redisUserDataPrefix;


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
        userEntity.setDelTag(YesOrNoEnum.NO.getStatus());
        for(String id : userIds){
            userEntity.setId(id);
            userMapper.updateById(userEntity);
            // 删除用户的同时，解绑角色
            LambdaQueryWrapper<UserRoleEntity> userRoleEntityLambdaQueryWrapper = Wrappers.<UserRoleEntity>lambdaQuery()
                    .eq(UserRoleEntity::getUserId,id);
            userRoleMapper.delete(userRoleEntityLambdaQueryWrapper);

            // 删除三方绑定
            LambdaQueryWrapper<ThirdUserEntity> thirdUserEntityLambdaQueryWrapper = Wrappers.<ThirdUserEntity>lambdaQuery()
                    .eq(ThirdUserEntity::getUserId,id);
            thirdUserMapper.delete(thirdUserEntityLambdaQueryWrapper);

            // 重新把用户刷入redis中
            this.userToRedis(true);
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
        userEntity.setDelTag(YesOrNoEnum.YES.getStatus());
        if(userEntity.getStatus() == null){
            userEntity.setStatus(YesOrNoEnum.YES.getStatus());
        }
        String userId = UUIDUtils.UUIDReplace();
        userEntity.setId(userId);
        int count = userMapper.insert(userEntity);
        if(count > 0){
            this.changeUserRole(userId,user.getRoleIds());
            this.userToRedis(true);
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
            this.userToRedis(true);
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
    public int updateCurrentUser(UserRequest user) {
        user.setId(SecurityUtils.getLoginUser().getId());
        int count = userMapper.updateUser(user);
        userTokenService.refreshUser();
        return count;
    }

    @Override
    public LoginUser getUserSelf() {
        LoginUser u = userTokenService.getUserSelf();
        if(Objects.nonNull(u)){
            u.setPermissions(this.menuService.getMenuPermission(u.getId()));
            this.roleService.setLoginUserRoleService(u);
        }
        userTokenService.refreshUser();
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

    /**
     * 根据角色Id获取用户
     *
     * @param roleId
     * @return
     */
    @Override
    public List<UserResponse> getByRoleId(String roleId) {
        List<UserResponse> userResponseList = userMapper.getByRoleId(roleId);
        return userResponseList;
    }

    /**
     * 初始化默认用户
     *
     * @param loginUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initDefaultUser(LoginUser loginUser) {
        String thirdUserId = loginUser.getThirdUserId();
        String userId = loginUser.getId();
        UserEntity userEntity = BeanUtils.copy(loginUser, UserEntity.class);
        userEntity.setId(userId);
        userEntity.setDelTag(YesOrNoEnum.YES.getStatus());
        // 设置登录密码
        String secretPassword = SecretUtils.encryptPassword("123456");
        userEntity.setPassword(secretPassword);
        int count = userMapper.insert(userEntity);
        if(count > 0){
            // 关联用户角色（默认游客角色）
            LambdaQueryWrapper<RoleEntity> queryWrapper = Wrappers.<RoleEntity>lambdaQuery()
                    .eq(RoleEntity::getDataScope, DataScopeEnum.VISITOR.getDataScope());
            Optional<RoleEntity> first = roleMapper.selectList(queryWrapper).stream().findFirst();
            if(!first.isPresent()){
                throw new SoException("设置默认用户没有此角色对应的数据权限："+DataScopeEnum.VISITOR.getDesc());
            }
            RoleEntity roleEntity = first.get();
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setUserId(userId);
            userRoleEntity.setRoleId(roleEntity.getId());
            userRoleMapper.insert(userRoleEntity);

            // 关联三方
            ThirdUserEntity thirdUserEntity = new ThirdUserEntity();
            thirdUserEntity.setUserId(userId);
            LoginTypeEnum accountTypeEnum = LoginTypeEnum.getLoginTypeEnum(loginUser.getLoginType());
            switch (accountTypeEnum){
                case QQ:
                    thirdUserEntity.setQqUserId(thirdUserId);
                    thirdUserEntity.setQqUserName(loginUser.getNickName());
                    break;
                case ZHI_FU_BAO:
                    thirdUserEntity.setAliUserId(thirdUserId);
                    thirdUserEntity.setAliUserName(loginUser.getNickName());
                    break;
                default:
                    break;
            }
            thirdUserMapper.insert(thirdUserEntity);
        }
    }

    /**
     * 系统用户转存到redis中
     * @param isAsync
     * @return
     */
    @Override
    public int userToRedis(boolean isAsync) {
        if(isAsync){
            xianYumTaskExecutor.execute(()->{
                try {
                    Thread.sleep(2000L);
                }catch (Exception e){

                }
                this.userToRedis();
            });
            return 1;
        }else{
            return this.userToRedis();
        }
    }

    /**
     * 执行系统用户刷入redis中
     * @return
     */
    @Override
    public int userToRedis() {
        redisUtils.del(redisUserDataPrefix);
        LambdaQueryWrapper<UserEntity> queryWrapper = Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getDelTag, YesOrNoEnum.YES.getStatus())
                .eq(UserEntity::getStatus, YesOrNoEnum.YES.getStatus());
        List<UserEntity> userEntities = userMapper.selectList(queryWrapper);
        for(UserEntity item : userEntities){
            redisUtils.hSet(redisUserDataPrefix,item.getId(), JSONObject.toJSONString(item));
        }
        return userEntities.size();
    }

    /**
     * 绑定qq用户
     *
     * @param authCode
     * @return
     */
    @Override
    public boolean bindQqUser(String authCode) {
        if(StringUtil.isBlank(authCode)){
            return false;
        }
        String accessToken = qqNetService.getAccessToken(authCode);
        QqUserEntity qqUserEntity = qqNetService.getUserId(accessToken);
        String openUserId = qqUserEntity.getUserId();
        if(StringUtil.isBlank(openUserId)){
            return false;
        }
        String userId = SecurityUtils.getLoginUser().getId();
        LambdaQueryWrapper<ThirdUserEntity> queryWrapper= Wrappers.<ThirdUserEntity>lambdaQuery()
                .eq(ThirdUserEntity::getQqUserId, openUserId);
        ThirdUserEntity thirdUserEntity = thirdUserMapper.selectOne(queryWrapper);
        int count;
        if(Objects.isNull(thirdUserEntity)){
            ThirdUserEntity saveThirdUserEntity = new ThirdUserEntity();
            saveThirdUserEntity.setUserId(userId);
            saveThirdUserEntity.setQqUserId(openUserId);
            saveThirdUserEntity.setQqUserName(EmojiUtils.filterEmoji(qqUserEntity.getNickname()));
            count = thirdUserMapper.insert(saveThirdUserEntity);
        }else{
            thirdUserEntity.setQqUserId(openUserId);
            thirdUserEntity.setUserId(userId);
            thirdUserEntity.setQqUserName(EmojiUtils.filterEmoji(qqUserEntity.getNickname()));
            count = thirdUserMapper.updateById(thirdUserEntity);
        }
        return count > 0;
    }

    /**
     * 绑定支付宝用户
     *
     * @param authCode
     * @return
     */
    @Override
    public boolean binAlidUser(String authCode) {
        AlipayUserInfoShareResponse aLiUserInfo = this.aliNetService.getALiUserInfo(
                this.aliNetService.getAccessToken(authCode));
        if(!aLiUserInfo.isSuccess()){
            return false;
        }
        // 获取到阿里的openUserId
        String openUserId = aLiUserInfo.getUserId();
        String userId = SecurityUtils.getLoginUser().getId();
        LambdaQueryWrapper<ThirdUserEntity> queryWrapper= Wrappers.<ThirdUserEntity>lambdaQuery()
                .eq(ThirdUserEntity::getAliUserId, openUserId);
        ThirdUserEntity thirdUserEntity = thirdUserMapper.selectOne(queryWrapper);
        int count;
        if(Objects.isNull(thirdUserEntity)){
            ThirdUserEntity saveThirdUserEntity = new ThirdUserEntity();
            saveThirdUserEntity.setUserId(userId);
            saveThirdUserEntity.setAliUserId(openUserId);
            saveThirdUserEntity.setAliUserName(EmojiUtils.filterEmoji(aLiUserInfo.getNickName()));
            count = thirdUserMapper.insert(saveThirdUserEntity);
        }else{
            thirdUserEntity.setAliUserId(openUserId);
            thirdUserEntity.setUserId(userId);
            thirdUserEntity.setAliUserName(EmojiUtils.filterEmoji(aLiUserInfo.getNickName()));
            count = thirdUserMapper.updateById(thirdUserEntity);
        }
        return count > 0;
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
            ThirdUserEntity aliUserEntity = thirdUserMapper.selectOne(new QueryWrapper<ThirdUserEntity>().eq("ali_user_id",aLiUserInfo.getUserId()));
            String nickName = EmojiUtils.filterEmoji(aLiUserInfo.getNickName());
            //如果没有查到与系统用户关联的，自动生成一个用户信息
            if(aliUserEntity == null ){
                loginUser.setId(UUIDUtils.UUIDReplace());
                loginUser.setThirdUserId(aLiUserInfo.getUserId());
                loginUser.setUsername(UUIDUtils.getCodeChar(5));
                loginUser.setNickName(nickName);
                loginUser.setStatus(YesOrNoEnum.YES.getStatus());
                loginUser.setAvatar(aLiUserInfo.getAvatar());
                loginUser.setLoginType(LoginTypeEnum.ZHI_FU_BAO.getAccountType());
                loginUser.setSex(0);
                SpringUtils.getBean(UserService.class).initDefaultUser(loginUser);
            }else{
                LambdaQueryWrapper<UserEntity> queryWrapper = Wrappers.<UserEntity>lambdaQuery()
                        .eq(UserEntity::getId,aliUserEntity.getUserId())
                        .eq(UserEntity::getDelTag, YesOrNoEnum.YES.getStatus())
                        .eq(UserEntity::getStatus, YesOrNoEnum.YES.getStatus());
                UserEntity userEntity = userMapper.selectOne(queryWrapper);
                if(Objects.isNull(userEntity)){
                    throw new SoException("账号不存在或被禁用！");
                }
                if(StringUtil.isNotBlank(nickName) && !Objects.equals(nickName,aliUserEntity.getAliUserName())){
                    aliUserEntity.setAliUserName(nickName);
                    thirdUserMapper.updateById(aliUserEntity);
                }
                loginUser = BeanUtils.copy(userEntity,LoginUser.class);
            }
            loginUser.setLoginType(LoginTypeEnum.ZHI_FU_BAO.getAccountType());
            return loginUser;
        }
        return null;
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
            ThirdUserEntity qqThirdUserEntity = thirdUserMapper.selectOne(new QueryWrapper<ThirdUserEntity>().eq("qq_user_id",qqUserEntity.getUserId()));
            String nickName = EmojiUtils.filterEmoji(qqUserEntity.getNickname());
            if(qqThirdUserEntity == null ){
                loginUser.setId(UUIDUtils.UUIDReplace());
                loginUser.setThirdUserId(qqUserEntity.getUserId());
                loginUser.setUsername(UUIDUtils.getCodeChar(5));
                loginUser.setNickName(nickName);
                loginUser.setStatus(YesOrNoEnum.YES.getStatus());
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
                loginUser.setLoginType(LoginTypeEnum.QQ.getAccountType());
                SpringUtils.getBean(UserService.class).initDefaultUser(loginUser);
            }else{
                LambdaQueryWrapper<UserEntity> queryWrapper = Wrappers.<UserEntity>lambdaQuery()
                        .eq(UserEntity::getId,qqThirdUserEntity.getUserId())
                        .eq(UserEntity::getDelTag, YesOrNoEnum.YES.getStatus())
                        .eq(UserEntity::getStatus, YesOrNoEnum.YES.getStatus());
                UserEntity userEntity = userMapper.selectOne(queryWrapper);
                if(Objects.isNull(userEntity)){
                    throw new SoException("账号不存在或被禁用！");
                }
                if(StringUtil.isNotBlank(nickName) && !Objects.equals(nickName,qqThirdUserEntity.getAliUserName())){
                    qqThirdUserEntity.setAliUserName(nickName);
                    thirdUserMapper.updateById(qqThirdUserEntity);
                }
                loginUser = BeanUtils.copy(userEntity,LoginUser.class);
                loginUser.setLoginType(LoginTypeEnum.QQ.getAccountType());
            }
            return loginUser;
        }
        return null;
    }
}
