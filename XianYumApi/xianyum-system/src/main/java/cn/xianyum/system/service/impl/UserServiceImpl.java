package cn.xianyum.system.service.impl;

import cn.xianyum.common.config.XianYumConfig;
import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.DeleteTagEnum;
import cn.xianyum.common.enums.LoginTypeEnum;
import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.enums.UserStatusEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.*;
import cn.xianyum.system.common.utils.SecretUtils;
import cn.xianyum.system.dao.ThirdUserMapper;
import cn.xianyum.system.dao.UserMapper;
import cn.xianyum.system.entity.po.QqUserEntity;
import cn.xianyum.system.entity.po.SystemConstantEntity;
import cn.xianyum.system.entity.po.ThirdUserEntity;
import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UpdatePasswordRequest;
import cn.xianyum.system.entity.request.UserRequest;
import cn.xianyum.system.entity.response.UserResponse;
import cn.xianyum.system.service.*;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

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

    @Override
    public PageResponse<UserResponse> getPage(UserRequest user) {
        Page<UserEntity> page = new Page<>(user.getPageNum(),user.getPageSize());
        //查询总记录数
        page.setSearchCount(true);
        if(SecurityUtils.getLoginUser().getPermission() != PermissionEnum.ADMIN.getStatus()){
            user.setId(SecurityUtils.getLoginUser().getId());
        }else{
            user.setId(null);
        }
        List<UserEntity> list = userMapper.queryAll(user, page);
        page.setRecords(list);
        return PageResponse.of(page,UserResponse.class);
    }

    @Override
    public UserEntity queryByUserName(String username) {
        UserEntity user = userMapper.selectOne(
                new QueryWrapper<UserEntity>().eq("username",username)
        );
        return user;
    }

    @Override
    public void deleteById(String[] userIds) {

        SecurityUtils.allowAdminAuth();

        UserEntity userEntity = new UserEntity();
        userEntity.setDelTag(UserStatusEnum.BAN.getStatus());
        for(String id : userIds){
            userEntity.setId(id);
            userMapper.updateById(userEntity);
        }
    }

    @Override
    public UserEntity selectOneById(String id) {
        return userMapper.selectById(id);
    }

    @Override
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
        userEntity.setId(UUIDUtils.UUIDReplace());
        int count = userMapper.insert(userEntity);
        return count;
    }

    @Override
    public int update(UserRequest user) {
        UserEntity userEntity = BeanUtils.copy(user, UserEntity.class);
        List<UserEntity> repeatList = userMapper.getList(user).stream().filter(p -> !user.getId().equals(p.getId())).collect(Collectors.toList());
        if(repeatList != null && repeatList.size() >0){
            throw new SoException("用户名或手机号已被使用！");
        }
        if(StringUtil.isNotEmpty(user.getPassword())){
            userEntity.setPassword(SecretUtils.encryptPassword(user.getPassword()));
        }
        return userMapper.updateById(userEntity);
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
    public UserEntity getUserByQq(String authCode) {
        if(StringUtil.isBlank(authCode)){
            return null;
        }
        String accessToken = qqNetService.getAccessToken(authCode);
        QqUserEntity qqUserEntity = qqNetService.getUserId(accessToken);
        if(StringUtil.isNotBlank(qqUserEntity.getUserId())){
            UserEntity userEntity = new UserEntity();
            ThirdUserEntity aliUserEntity = aliUserMapper.selectOne(new QueryWrapper<ThirdUserEntity>().eq("qq_user_id",qqUserEntity.getUserId()));
            if(aliUserEntity == null ){
                userEntity.setId(qqUserEntity.getUserId());
                userEntity.setUsername(EmojiUtils.filterEmoji(qqUserEntity.getNickname()));
                userEntity.setStatus(UserStatusEnum.ALLOW.getStatus());
                userEntity.setPermission(PermissionEnum.VISITOR.getStatus());
                if("女".equals(qqUserEntity.getGender())){
                    userEntity.setSex(1);
                }else{
                    userEntity.setSex(0);
                }
                // 现在qq返回的是http连接，实际https也是支持的，这里替换下
                if(StringUtil.isNotEmpty(qqUserEntity.getFigureurl_qq_2())){
                    userEntity.setAvatar(qqUserEntity.getFigureurl_qq_2().replace("http","https"));
                }else{
                    userEntity.setAvatar(qqUserEntity.getFigureurl_qq_1().replace("http","https"));
                }
            }else{
                userEntity= userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .eq("id",aliUserEntity.getUserId())
                        .eq("del_tag",UserStatusEnum.ALLOW.getStatus()));
            }
            userEntity.setLoginType(LoginTypeEnum.QQ.getLoginType());
            return userEntity;
        }
        return null;
    }

    @Override
    public Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        if(SecurityUtils.getLoginUser().getPermission() == PermissionEnum.ADMIN.getStatus()){
            for(PermissionEnum item : PermissionEnum.values()){
                permissions.add(item.getName());
            }
        }
        permissions.add(PermissionEnum.getNameByStatus(SecurityUtils.getLoginUser().getPermission()));
        return permissions;
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

    @Override
    public UserEntity getUserByAli(String authCode) {
        if(StringUtil.isBlank(authCode)){
            return null;
        }
        String accessToken = aliNetService.getAccessToken(authCode);
        AlipayUserInfoShareResponse aLiUserInfo = aliNetService.getALiUserInfo(accessToken);
        if(aLiUserInfo.isSuccess()){
            UserEntity userEntity = new UserEntity();
            ThirdUserEntity aliUserEntity = aliUserMapper.selectOne(new QueryWrapper<ThirdUserEntity>().eq("ali_user_id",aLiUserInfo.getUserId()));
            //如果没有查到与系统用户关联的，自动生成一个用户信息
            if(aliUserEntity == null ){
                userEntity.setId(aLiUserInfo.getUserId());
                userEntity.setUsername(EmojiUtils.filterEmoji(aLiUserInfo.getNickName()));
                userEntity.setStatus(UserStatusEnum.ALLOW.getStatus());
                userEntity.setAvatar(aLiUserInfo.getAvatar());
                userEntity.setPermission(PermissionEnum.VISITOR.getStatus());
            }else{
                userEntity= userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .eq("id",aliUserEntity.getUserId())
                        .eq("del_tag",UserStatusEnum.ALLOW.getStatus()));
            }
            userEntity.setLoginType(LoginTypeEnum.ZHI_FU_BAO.getLoginType());
            return userEntity;
        }
        return null;
    }
}
