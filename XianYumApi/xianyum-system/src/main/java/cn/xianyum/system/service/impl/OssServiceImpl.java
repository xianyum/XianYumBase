package cn.xianyum.system.service.impl;


import cn.xianyum.common.utils.FileUtils;
import cn.xianyum.system.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



/**
 * @author zhangwei
 * @date 2019/11/30 14:55
 * @desc
 */
@Service
@Slf4j
public class OssServiceImpl implements OssService {

    @Override
    public byte[] getImage(String path) {
        byte[] avatarImage = FileUtils.getAvatarImage(path);
        return avatarImage;
    }
}
