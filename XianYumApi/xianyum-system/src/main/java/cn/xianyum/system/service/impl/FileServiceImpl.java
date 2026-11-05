package cn.xianyum.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.RedisUtils;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.file.FileDetailResponse;
import cn.xianyum.system.service.FileDetailService;
import cn.xianyum.system.service.FileService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
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



    @Value("${dromara.x-file-storage.custom-domain}")
    private String customDomain;



    @Override
    public FileDetailResponse uploadFile(MultipartFile file) {
        return uploadFile(file, buildDefaultPath());
    }

    @Override
    public FileDetailResponse uploadFile(MultipartFile file, String path) {
        if (file.isEmpty()) {
            throw new SoException("上传的文件为空");
        }
        FileInfo fileInfo = this.fileStorageService.of(file)
                .setPath(buildPath(path))
                .upload();
        return this.selectFileById(fileInfo.getId(), true);
    }

    @Override
    public FileDetailResponse uploadFile(File file) {
        return uploadFile(file, buildDefaultPath());
    }

    @Override
    public FileDetailResponse uploadFile(File file, String path) {
        if (file == null || !file.exists()) {
            throw new SoException("本地文件不存在: " + (file != null ? file.getAbsolutePath() : "null"));
        }
        FileInfo fileInfo = this.fileStorageService.of(file)
                .setPath(buildPath(path))
                .upload();
        return this.selectFileById(fileInfo.getId(), true);
    }

    private String buildDefaultPath() {
        return DateUtils.format(new Date(), DateUtils.DATE_PATTERN) + Constants.SLASH;
    }

    private String buildPath(String path) {
        if (StrUtil.isBlank(path)) {
            return buildDefaultPath();
        }
        return path.endsWith(Constants.SLASH) ? path : path + Constants.SLASH;
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
        if (StrUtil.isEmpty(fileId)) {
            return null;
        }
        String redisKey = String.format(RedisKeyEnum.FILE_PRESIGNED_URL.getKey(), fileId);

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
        List<? extends FileStorageProperties.QiniuKodoConfig> qiniuKodo = fileStorageService.getProperties().getQiniuKodo();
        if (StrUtil.isNotBlank(presignedUrl)) {
            String domain = qiniuKodo.get(0).getDomain();
            response.setFileUrl(presignedUrl.replaceAll(domain,customDomain));
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
