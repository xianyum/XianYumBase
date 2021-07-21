package cn.xianyum.system.service;


import cn.xianyum.system.entity.po.OssFileEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangwei
 * @date 2019/11/30 14:54
 * @desc
 */
public interface OssService {

    OssFileEntity upload(MultipartFile file);

    String getWebUpToken();

    byte[] getImage(String path);
}
