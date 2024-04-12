package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.RoleMenuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * 角色菜单关联表(RoleMenu)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-01 20:00:35
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenuEntity> {

    int savBatchRoleMenus(List<RoleMenuEntity> roleMenuEntityList);
}

