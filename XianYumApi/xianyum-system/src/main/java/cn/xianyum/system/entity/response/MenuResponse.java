package cn.xianyum.system.entity.response;

import lombok.Data;

import java.util.List;

/**
 * @author zhangwei
 * @date 2023/9/19 12:06
 */
@Data
public class MenuResponse {

    /**
     * 路由名字
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private boolean hidden;

    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 路由参数：如 {"id": 1, "name": "ry"}
     */
    private String query;

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     */
    private Boolean alwaysShow;

    /**
     * 其他元素
     */
    private MenuMetaResponse meta;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 子路由
     */
    private List<MenuResponse> children;
}
