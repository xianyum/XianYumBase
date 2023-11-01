package cn.xianyum.system.dao;

import cn.xianyum.system.entity.response.RoleResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.xianyum.system.entity.po.RoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色管理(Role)表数据库访问层
 *
 * @author makejava
 * @since 2023-10-31 19:57:15
 */
public interface RoleMapper extends BaseMapper<RoleEntity> {

    List<RoleResponse> getRoleByUserId(@Param("userId") String userId);
}

