package com.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.entity.po.MenuEntity;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/5/25 19:29
 * @email 80616059@qq.com
 */
public interface MenuMapper extends BaseMapper<MenuEntity> {

    List<MenuEntity> queryListParentId(Long parentId);

    List<Long> queryMenuID();
}
