package cn.xianyum.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.common.entity.file.FileDetailResponse;
import cn.xianyum.system.service.FileDetailService;
import cn.xianyum.system.service.FileService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.Objects;

/**
 * @author xianyum
 * @date 2026/1/19 22:50
 */

@Slf4j
@Service
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
    public FileDetailResponse uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new SoException("上传的文件为空");
        }
        FileInfo fileInfo = this.fileStorageService.of(file)
                .setPath(DateUtils.format(new Date(), DateUtils.DATE_PATTERN) + Constants.SLASH)
                .upload();
        return this.selectFileById(fileInfo.getId(), true);
    }

    /**
     * 查询文件信息
     *
     * @param fileId
     * @param isCached true 带缓存 false 不带缓存
     * @return
     */
    @Override
    public FileDetailResponse selectFileById(String fileId, boolean isCached) {
        if (StringUtil.isEmpty(fileId)) {
            return null;
        }
        String redisKey = String.format(presignedUrlPrefix, fileId);

        if (isCached && redisUtils.hasKey(redisKey)) {
            FileDetailResponse response = JSONObject.parseObject(redisUtils.getString(redisKey), FileDetailResponse.class);
            response.setExpireTime(new Date().getTime() + redisUtils.getExpire(redisKey) * 1000);
            return response;
        }
        FileInfo fileInfo = fileDetailService.selectFileById(fileId);
        if (Objects.isNull(fileInfo)) {
            return null;
        }
        String presignedUrl = fileStorageService.generatePresignedUrl(fileInfo, DateUtil.offsetHour(new Date(), 1));
        FileDetailResponse response = BeanUtil.copyProperties(fileInfo, FileDetailResponse.class);
        if (StringUtil.isNotBlank(presignedUrl)) {
            response.setFileUrl(presignedUrl);
            response.setExpireTime(new Date().getTime() + 3600 * 1000L);
            redisUtils.setMin(redisKey, JSONObject.toJSONString(response), 60);
        }
        return response;
    }

    /**
     * 获取预签名url
     *
     * @param fileId
     * @return
     */
    @Override
    public String presignedUrl(String fileId) {
        FileDetailResponse response = this.selectFileById(fileId, true);
        return Objects.nonNull(response) ? response.getFileUrl() : null;
    }

}
