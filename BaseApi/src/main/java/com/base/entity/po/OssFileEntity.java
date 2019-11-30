package com.base.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * 文件实体类
 * @author zhangwei
 * @date 2019/11/30 15:38
 * @desc
 */
@Data
public class OssFileEntity {

    private String id;

    private String url;

    private String fileName;

    private Long size;

    private String remark;

    private Date createTime;

    private Long createUserId;

    private String createUserName;
}
