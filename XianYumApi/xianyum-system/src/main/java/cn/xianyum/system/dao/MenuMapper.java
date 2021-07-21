package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.MenuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhangwei
 * @date 2019/5/25 19:29
 * @email 80616059@qq.com
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    List<MenuEntity> queryListParentId(Long parentId);

    List<Long> queryMenuID();
}
