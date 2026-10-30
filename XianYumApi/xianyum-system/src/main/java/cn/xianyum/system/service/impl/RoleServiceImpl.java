package cn.xianyum.system.service.impl;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.entity.UserRoleCache;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.enums.DataScopeEnum;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.RedisUtils;
import com.alibaba.fastjson2.JSON;
import cn.hutool.core.bean.BeanUtil;
import cn.xianyum.common.utils.SecurityUtils;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.system.dao.RoleMenuMapper;
import cn.xianyum.system.dao.RoleMapper;
import cn.xianyum.system.dao.UserRoleMapper;
import cn.xianyum.system.entity.po.RoleEntity;
import cn.xianyum.system.entity.po.RoleMenuEntity;
import cn.xianyum.system.entity.po.UserRoleEntity;
import cn.xianyum.system.entity.request.RoleRequest;
import cn.xianyum.system.entity.response.RoleResponse;
import cn.xianyum.system.infra.event.RoleMenuCacheChangeEvent;
import cn.xianyum.system.infra.event.UserRoleCacheChangeEvent;
import cn.xianyum.system.service.MenuService;
import cn.xianyum.system.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MenuService menuService;

    @Override
    public PageResponse<RoleResponse> getPage(RoleRequest request) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = Wrappers.<RoleEntity>lambdaQuery()
                .like(StrUtil.isNotEmpty(request.getRoleCode()), RoleEntity::getRoleCode, request.getRoleCode())
                .like(StrUtil.isNotEmpty(request.getRoleName()), RoleEntity::getRoleName, request.getRoleName())
                .eq(Objects.nonNull(request.getStatus()), RoleEntity::getStatus, request.getStatus())
                .orderByAsc(RoleEntity::getRoleSort);
        Page<RoleEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<RoleEntity> pageResult = roleMapper.selectPage(page, queryWrapper);
        return PageResponse.of(pageResult, RoleResponse.class);
    }

    @Override
    public RoleResponse getById(Long id) {
        RoleEntity result = roleMapper.selectById(id);
        RoleResponse response = BeanUtil.toBean(result, RoleResponse.class);
        return Objects.isNull(response) ? new RoleResponse() : response;
    }

    @Override
    public Integer save(RoleRequest request) {
        RoleEntity bean = BeanUtil.toBean(request, RoleEntity.class);
        return roleMapper.insert(bean);
    }

    @Override
    public Integer update(RoleRequest request) {
        if (Objects.isNull(request.getId())) {
            throw new SoException("id不能为空");
        }
        if (request.getId().equals(1L)) {
            throw new SoException("不能更新admin权限！");
        }
        RoleEntity bean = BeanUtil.toBean(request, RoleEntity.class);
        Integer result = roleMapper.updateById(bean);
        this.clearRoleCacheByRoleId(request.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(Long[] ids) {
        int resultCount = 0;
        for (Long id : ids) {
            if (id.equals(1L)) {
                throw new SoException("不能删除admin权限！");
            }
            this.checkRoleIsDelete(id);
            resultCount = roleMapper.deleteById(id) + resultCount;
            LambdaQueryWrapper<RoleMenuEntity> roleMenuEntityLambdaQueryWrapper = Wrappers.<RoleMenuEntity>lambdaQuery()
                    .eq(RoleMenuEntity::getRoleId, id);
            roleMenuMapper.delete(roleMenuEntityLambdaQueryWrapper);
            this.clearRoleCacheByRoleId(id);
            this.publishRoleMenuChangeByRoleId(id);
        }
        return resultCount;
    }

    @Override
    public Integer changeStatus(RoleRequest request) {
        if (Objects.isNull(request.getId()) || Objects.isNull(request.getStatus())) {
            throw new SoException("更新角色状态失败！");
        }
        if (request.getId().equals(1L)) {
            throw new SoException("不能更新admin权限！");
        }
        RoleEntity bean = new RoleEntity();
        bean.setId(request.getId());
        bean.setStatus(request.getStatus());
        Integer result = roleMapper.updateById(bean);
        this.clearRoleCacheByRoleId(request.getId());
        return result;
    }

    @Override
    public Integer changeDataScope(RoleRequest request) {
        if (Objects.isNull(request.getId()) || Objects.isNull(request.getDataScope())) {
            throw new SoException("更新角色状态失败！");
        }
        if (request.getId().equals(1L)) {
            throw new SoException("不能更新admin权限！");
        }
        RoleEntity bean = new RoleEntity();
        bean.setId(request.getId());
        bean.setDataScope(request.getDataScope());
        Integer result = roleMapper.updateById(bean);
        this.clearRoleCacheByRoleId(request.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer authorizationMenu(RoleRequest request) {
        if (Objects.isNull(request.getId()) || Objects.isNull(request.getMenuIds())) {
            throw new SoException("授权菜单权限失败！");
        }
        if (request.getId().equals(1L)) {
            throw new SoException("不能更新admin权限！");
        }
        LambdaQueryWrapper<RoleMenuEntity> queryWrapper = Wrappers.<RoleMenuEntity>lambdaQuery()
                .eq(RoleMenuEntity::getRoleId, request.getId());
        roleMenuMapper.delete(queryWrapper);
        List<RoleMenuEntity> roleMenuEntityList = new ArrayList<>();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        for (Long menuId : request.getMenuIds()) {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setRoleId(request.getId());
            roleMenuEntity.setMenuId(menuId);
            roleMenuEntity.setCreateTime(new Date());
            roleMenuEntity.setUpdateTime(new Date());
            roleMenuEntity.setCreateBy(loginUser.getId());
            roleMenuEntity.setCreateByName(loginUser.getUsername());
            roleMenuEntity.setUpdateBy(loginUser.getId());
            roleMenuEntity.setUpdateByName(loginUser.getUsername());
            roleMenuEntityList.add(roleMenuEntity);
        }
        Integer result = roleMenuMapper.savBatchRoleMenus(roleMenuEntityList);
        if (result > 0) {
            this.publishRoleMenuChangeByRoleId(request.getId());
        }
        return result;
    }

    @Override
    public List<RoleResponse> getList(RoleRequest request) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = Wrappers.<RoleEntity>lambdaQuery()
                .eq(RoleEntity::getStatus, 0)
                .orderByAsc(RoleEntity::getRoleSort);
        List<RoleEntity> roleEntities = roleMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(roleEntities, RoleResponse.class);
    }


    @Override
    public UserRoleCache selectUserRoleCacheByUserId(String userId) {
        Object cacheObj = redisUtils.hGet(RedisKeyEnum.ROLE_INFO_BY_USER_KEY.getKey(), userId);
        if (Objects.nonNull(cacheObj)) {
            UserRoleCache roleCache = JSON.parseObject(cacheObj.toString(), UserRoleCache.class);
            return roleCache;
        }
        UserRoleCache roleCache = new UserRoleCache();
        List<RoleResponse> roleList = roleMapper.getRoleByUserId(userId);
        if (CollUtil.isNotEmpty(roleList)) {
            Set<String> roleCodes = roleList.stream().map(RoleResponse::getRoleCode).collect(Collectors.toSet());
            Set<String> roleNames = roleList.stream().map(RoleResponse::getRoleName).collect(Collectors.toSet());
            // 数字越小说明数据权限越大
            Integer minDataScopeInt = roleList.stream().map(RoleResponse::getDataScope).filter(Objects::nonNull).map(Integer::valueOf).min(Integer::compareTo).orElse(null);
            roleCache.setRoleCodes(roleCodes);
            roleCache.setRoleNames(roleNames);
            roleCache.setDataScopeEnum(DataScopeEnum.getDataScope(String.valueOf(minDataScopeInt)));
            redisUtils.hSet(RedisKeyEnum.ROLE_INFO_BY_USER_KEY.getKey(), userId, JSON.toJSONString(roleCache));
        }
        return roleCache;
    }

    @Override
    public void checkRoleIsDelete(Long id) {
        LambdaQueryWrapper<UserRoleEntity> queryWrapper = Wrappers.<UserRoleEntity>lambdaQuery()
                .eq(UserRoleEntity::getRoleId, id);
        Long userCount = userRoleMapper.selectCount(queryWrapper);
        if (userCount > 0) {
            throw new SoException(String.format("当前有%s名用户绑定此角色，请解绑后在删除！", userCount));
        }
    }

    /**
     * 清除角色缓存
     * @param roleId
     */
    private void clearRoleCacheByRoleId(Long roleId) {
        LambdaQueryWrapper<UserRoleEntity> queryWrapper = Wrappers.<UserRoleEntity>lambdaQuery()
                .eq(UserRoleEntity::getRoleId, roleId);
        List<UserRoleEntity> userRoles = userRoleMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(userRoles)) {
            for (UserRoleEntity ur : userRoles) {
                SpringUtil.publishEvent(new UserRoleCacheChangeEvent(ur.getUserId()));
            }
        }
    }

    /**
     * 角色菜单缓存事件
     * @param roleId
     */
    private void publishRoleMenuChangeByRoleId(Long roleId) {
        RoleEntity roleEntity = roleMapper.selectById(roleId);
        if (Objects.nonNull(roleEntity) && StrUtil.isNotBlank(roleEntity.getRoleCode())) {
            SpringUtil.publishEvent(new RoleMenuCacheChangeEvent(Set.of(roleEntity.getRoleCode())));
        }
    }
}
