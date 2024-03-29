package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.MenuEntity;
import cn.xianyum.system.entity.request.MenuRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * @author zhangwei
 * @date 2019/5/25 19:29
 * @email 80616059@qq.com
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    List<MenuEntity> selectMenuTreeByUserId(@RequestParam("userId") String userId);

    List<MenuEntity> selectMenuList(MenuRequest menuRequest);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    MenuEntity checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);

    int hasChildByMenuId(Long menuId);

    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);

    Set<String> selectMenuPermsByUserId(@Param("userId") String userId);
}
