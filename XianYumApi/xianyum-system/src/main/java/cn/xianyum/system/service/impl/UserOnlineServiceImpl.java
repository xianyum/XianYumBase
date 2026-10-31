package cn.xianyum.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.DateUtils;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.system.dao.UserOnlineMapper;
import cn.xianyum.system.entity.po.UserOnlineEntity;
import cn.xianyum.system.entity.request.UserOnlineRequest;
import cn.xianyum.system.entity.response.UserOnlineResponse;
import cn.xianyum.system.service.UserOnlineService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;

/**
 * @author zhangwei
 * @date 2021/1/28 22:03
 */
@Service
@Slf4j
public class UserOnlineServiceImpl implements UserOnlineService {

    @Resource
    private UserOnlineMapper userOnlineMapper;

    @Override
    public PageResponse<UserOnlineResponse> queryPage(UserOnlineRequest request) {
        // 清理已过期的在线记录
        userOnlineMapper.deleteExpired();
        LambdaQueryWrapper<UserOnlineEntity> wrapper = Wrappers.<UserOnlineEntity>lambdaQuery()
                .gt(UserOnlineEntity::getExpireTime, new Date())
                .orderByDesc(UserOnlineEntity::getId);
        if (StrUtil.isNotEmpty(request.getUsername())) {
            wrapper.like(UserOnlineEntity::getUsername, request.getUsername());
        }
        Page<UserOnlineEntity> page = userOnlineMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return PageResponse.of(page, UserOnlineResponse.class,
                (response, entity) -> response.setExpireStr(DateUtils.formatExpire(entity.getExpireTime()))
        );
    }

    @Override
    public void delete(String[] tokenIds) {
        if (tokenIds == null || tokenIds.length == 0) {
            throw new SoException("踢出失败");
        }
        for (String token : tokenIds) {
            try {
                // 让 Sa-Token 也下线该会话，防止客户端还能请求
                StpUtil.kickoutByTokenValue(token);
            } catch (Exception e) {
                log.warn("踢出 token={} 时 Sa-Token 下线失败, 可能会话已过期", token);
            }
        }
        // 从数据库删除
        userOnlineMapper.deleteByTokens(Arrays.asList(tokenIds));
    }
}
