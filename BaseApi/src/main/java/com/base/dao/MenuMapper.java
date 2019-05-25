package com.base.dao;

import com.base.entity.po.MenuEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper {
    List<MenuEntity> getAll(@Param("perms") String perms);
}
