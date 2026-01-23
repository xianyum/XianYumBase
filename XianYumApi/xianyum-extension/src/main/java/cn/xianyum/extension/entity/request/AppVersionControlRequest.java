package cn.xianyum.extension.entity.request;

import cn.xianyum.common.entity.base.BaseRequest;
import lombok.Data;

/**
 * APP版本管理(AppVersionControl)request请求实体
 *
 * @author zhangwei
 * @since 2026-01-23 17:28:11
 */
@Data
public class AppVersionControlRequest extends BaseRequest {
    /**
     * 主键id
     */
    private String id;

    /**
     * 应用唯一标识，区分不同APP
     */
    private String appId;

    /**
     * 版本号(如1.0.1)
     */
    private String version;

    /**
     * 更新包类型：1-热更新包，2-整包安装包
     */
    private Integer packageType;

    /**
     * 包文件id
     */
    private String packageFileId;

    /**
     * 更新日志内容
     */
    private String updateLog;

    /**
     * 更新标题
     */
    private String updateTitle;

    /**
     * 是否强制更新0-是，1-否
     */
    private Integer isForceUpdate;

    /**
     * 备注
     */
    private String remark;

}
