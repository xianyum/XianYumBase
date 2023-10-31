package cn.xianyum.system.entity.response;

import cn.xianyum.system.entity.po.MenuEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2023/10/31 21:37
 */
@Data
public class MenuTreeSelect {

    private Long id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MenuTreeSelect> children;


    public MenuTreeSelect(MenuEntity menu) {
        this.id = menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(MenuTreeSelect::new).collect(Collectors.toList());
    }
}
