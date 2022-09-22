package cn.xianyum.common.config;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.SystemConstantUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Objects;

/**
 * 读取项目相关配置
 * @author zhangwei
 * @date 2020/12/23 22:07
 */
@Data
public class XianYumConfig {

    /** 项目名称 */
    private String name;

    /** 版本 */
    private String version;

    /** 上传路径 */
    private String profile;

    /** 图像路径 */
    private String avatarUrl;

    /** 首页地址 */
    private String webUrl;

    public static XianYumConfig getXianYumConfig(){
        String systemJsonStr = SystemConstantUtils.getValueByKey("system");
        XianYumConfig xianYumConfig = JSONObject.parseObject(systemJsonStr, XianYumConfig.class);
        if(Objects.isNull(xianYumConfig)){
            throw new SoException("系统配置数据为空，请检查是否已经配置【system】系统常量");
        }
        return xianYumConfig;
    }


    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getXianYumConfig().getProfile() + "/avatar";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath()
    {
        return getXianYumConfig().getProfile() + "/upload";
    }
}
