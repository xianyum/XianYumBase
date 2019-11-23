package com.base.service.impl;

import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.common.exception.SoException;
import com.base.common.utils.AuthUserToken;
import com.base.common.utils.BeanUtils;
import com.base.common.utils.StringUtil;
import com.base.common.utils.UUIDUtils;
import com.base.dao.ThirdUserMapper;
import com.base.dao.UserMapper;
import com.base.entity.enums.DeleteTagEnum;
import com.base.entity.enums.UserStatusEnum;
import com.base.entity.po.ThirdUserEntity;
import com.base.entity.po.UserEntity;
import com.base.entity.request.UpdatePasswordRequest;
import com.base.entity.request.UserRequest;
import com.base.service.iservice.AliNetService;
import com.base.service.iservice.QqNetService;
import com.base.service.iservice.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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

    @Override
    public IPage<UserEntity> queryAll(UserRequest user) {
        Page<UserEntity> page = new Page<>(user.getPageNum(),user.getPageSize());
        //查询总记录数
        page.setSearchCount(true);
        List<UserEntity> list = userMapper.queryAll(user, page);
        page.setRecords(list);
        return page;
    }

    @Override
    public UserEntity queryByUserName(String username) {
        UserEntity user = userMapper.selectOne(
                new QueryWrapper<UserEntity>().eq("username",username)
        );
        return user;
    }

    @Override
    public void deleteById(Long[] userIds) {
        UserEntity userEntity = new UserEntity();
        userEntity.setDelTag(UserStatusEnum.BAN.getStatus());
        for(Long id : userIds){
            userEntity.setId(id);
            userMapper.updateById(userEntity);
        }
    }

    @Override
    public UserEntity selectOneById(UserRequest user) {
        return userMapper.selectById(user.getId());
    }

    @Override
    public int save(UserRequest user) {
        List<UserEntity> repeatList = userMapper.getList(user);
        if(repeatList != null && repeatList.size() >0){
            throw new SoException("用户名或手机号已被使用！");
        }
        UserEntity userEntity = BeanUtils.copy(user, UserEntity.class);
        userEntity.setCreateTime(new Date());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        userEntity.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
        userEntity.setSalt(salt);
        userEntity.setDelTag(DeleteTagEnum.Delete.getDeleteTag());
        if(userEntity.getStatus() == null){
            userEntity.setStatus(UserStatusEnum.ALLOW.getStatus());
        }
        int count = userMapper.insert(userEntity);
        return count;
    }

    @Override
    public int update(UserRequest user) {
        UserEntity userEntity = BeanUtils.copy(user, UserEntity.class);
        List<UserEntity> repeatList = userMapper.getList(user).stream().filter(p -> user.getId() != p.getId()).collect(Collectors.toList());
        if(repeatList != null && repeatList.size() >0){
            throw new SoException("用户名或手机号已被使用！");
        }
        if(StringUtil.isNotEmpty(user.getPassword())){
            String salt = RandomStringUtils.randomAlphanumeric(20);
            userEntity.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
            userEntity.setSalt(salt);
        }
        return userMapper.updateById(userEntity);
    }

    @Override
    public boolean updatePassword(UpdatePasswordRequest info) {
        UserEntity userEntity = AuthUserToken.getUser();
        String password = info.getPassword();
        if( !new Sha256Hash(password, userEntity.getSalt()).toHex().equals(userEntity.getPassword())){
            return false;
        }
        UserEntity user = new UserEntity();
        user.setId(userEntity.getId());
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        user.setPassword(new Sha256Hash(info.getNewPassword(), salt).toHex());
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
        String qqUserId = qqNetService.getUserId(accessToken);
        if(StringUtil.isBlank(qqUserId)){
            UserEntity userEntity = new UserEntity();
            ThirdUserEntity aliUserEntity = aliUserMapper.selectOne(new QueryWrapper<ThirdUserEntity>().eq("qq_user_id",qqUserId));
            if(aliUserEntity == null ){
                userEntity.setId(-1L);
                userEntity.setUsername(UUIDUtils.getUUID().substring(1,7));
                userEntity.setStatus(UserStatusEnum.ALLOW.getStatus());
            }else{
                userEntity= userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .eq("id",aliUserEntity.getUserId())
                        .eq("del_tag",UserStatusEnum.ALLOW.getStatus()));
            }
            return userEntity;
        }
        return null;
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
                userEntity.setId(-1L);
                userEntity.setUsername(aLiUserInfo.getNickName());
                userEntity.setStatus(UserStatusEnum.ALLOW.getStatus());
            }else{
                userEntity= userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .eq("id",aliUserEntity.getUserId())
                        .eq("del_tag",UserStatusEnum.ALLOW.getStatus()));
            }
            return userEntity;
        }
        return null;
    }
}
