package cn.xianyum.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 * @author zhangwei
 * @date 2020/12/23 22:07
 */
@Component
@ConfigurationProperties(prefix = "xianyum")
public class XianYumConfig {

    /** 项目名称 */
    private String name;

    /** 版本 */
    private String version;

    /** 上传路径 */
    private static String profile;

    /** 图像路径 */
    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public void setProfile(String profile)
    {
        XianYumConfig.profile = profile;
    }

    public static String getProfile() {
        return profile;
    }
    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + "/avatar";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }


}
