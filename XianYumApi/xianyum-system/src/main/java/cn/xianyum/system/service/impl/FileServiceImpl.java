package cn.xianyum.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.service.FileDetailService;
import cn.xianyum.system.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

/**
 * @author xianyum
 * @date 2026/1/19 22:50
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileDetailService fileDetailService;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${redis.file.presigned_url}")
    private String presignedUrlPrefix;

    /**
     * 上传文件，并返回文件ID
     *
     * @param file
     * @return
     */
    @Override
    public String uploadFile(MultipartFile file) {
        if(file.isEmpty()){
            throw new SoException("上传的文件为空");
        }
        FileInfo fileInfo = this.fileStorageService.of(file)
                .setPath(DateUtils.format(new Date(), DateUtils.DATE_PATTERN) + Constants.SLASH)
                .upload();
        return fileInfo.getId();
    }

    /**
     * 获取预签名url
     *
     * @param fileId
     * @return
     */
    @Override
    public String presignedUrl(String fileId) {
        if(StringUtil.isEmpty(fileId)){
            return null;
        }
        String redisKey = String.format(presignedUrlPrefix,fileId);
        if(redisUtils.hasKey(redisKey)){
            return redisUtils.getString(redisKey);
        }
        FileInfo fileInfo = fileDetailService.selectFileById(fileId);
        String presignedUrl = fileStorageService.generatePresignedUrl(fileInfo, DateUtil.offsetHour(new Date(), 1));
        if(StringUtil.isNotBlank(presignedUrl)){
            redisUtils.setMin(redisKey,presignedUrl,60);
        }
        return presignedUrl;
    }
}
