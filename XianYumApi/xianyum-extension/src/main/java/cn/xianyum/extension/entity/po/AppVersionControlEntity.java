package cn.xianyum.extension.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.xianyum.common.entity.base.BaseEntity;
import lombok.Data;

/**
 * APP版本管理(AppVersionControl)表实体类
 *
 * @author zhangwei
 * @since 2026-01-23 17:28:10
 */
@Data
@TableName("app_version_control")
public class AppVersionControlEntity extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.INPUT)
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
     * 更新包类型：wgt-热更新包，apk-整包安装包
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
