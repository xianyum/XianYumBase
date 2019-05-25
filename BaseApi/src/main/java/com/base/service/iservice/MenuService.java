package com.base.service.iservice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.base.entity.po.MenuEntity;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/5/25 19:35
 * @email 80616059@qq.com
 */
public interface MenuService extends IService<MenuEntity> {

    List<MenuEntity> getAll();
}
