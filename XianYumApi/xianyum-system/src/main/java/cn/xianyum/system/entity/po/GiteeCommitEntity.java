package cn.xianyum.system.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangwei
 * @date 2020/11/21 16:40
 */
@Data
@TableName(value = "gitee_commit")
public class GiteeCommitEntity extends BaseEntity {

    private String id;

    private String repositoryUrl;

    private String commitMessage;

    private String programId;

    private Date createTime;

    private String repositoryName;

}
