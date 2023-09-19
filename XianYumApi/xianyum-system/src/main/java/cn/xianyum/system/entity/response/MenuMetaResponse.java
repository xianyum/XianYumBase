package cn.xianyum.system.entity.response;

import cn.xianyum.common.utils.StringUtil;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2023/9/19 12:07
 */
@Data
public class MenuMetaResponse {

    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    /**
     * 设置为true，则不会被 <keep-alive>缓存
     */
    private boolean noCache;

    /**
     * 内链地址（http(s)://开头）
     */
    private String link;

    public MenuMetaResponse(String title, String icon, boolean noCache, String link) {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
        if (StringUtil.ishttp(link)) {
            this.link = link;
        }
    }

    public MenuMetaResponse(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public MenuMetaResponse(String title, String icon, String link) {
        this.title = title;
        this.icon = icon;
        this.link = link;
    }
}
