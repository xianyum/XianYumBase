package com.base.service.iservice;

import com.base.entity.po.OssFileEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangwei
 * @date 2019/11/30 14:54
 * @desc oss采用七牛云
 */
public interface OssService {
    OssFileEntity upload(MultipartFile file);
}
