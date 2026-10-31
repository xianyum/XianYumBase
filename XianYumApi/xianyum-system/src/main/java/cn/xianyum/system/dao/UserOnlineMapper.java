package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.UserOnlineEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 在线用户 Mapper
 */
public interface UserOnlineMapper extends BaseMapper<UserOnlineEntity> {

    /**
     * 根据 token 查询在线用户
     */
    UserOnlineEntity selectByToken(@Param("token") String token);

    /**
     * 根据 token 删除在线用户
     */
    int deleteByToken(@Param("token") String token);

    /**
     * 删除已过期的在线用户
     */
    int deleteExpired();

    /**
     * 批量根据 token 删除
     */
    int deleteByTokens(@Param("tokens") List<String> tokens);
}
