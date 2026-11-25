package cn.xianyum.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.enums.RedisKeyEnum;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.DateUtils;
import cn.xianyum.common.utils.IdGeneratorUtil;
import cn.xianyum.common.utils.RedisUtils;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.common.entity.file.FileDetailResponse;
import cn.xianyum.common.entity.file.PresignedUploadResponse;
import cn.xianyum.system.service.FileDetailService;
import cn.xianyum.system.service.FileService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.constant.Constant;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.platform.QiniuKodoFileStorage;
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

    /**
     * 生成七牛云表单直传凭证
     *
     * @param filename 文件名
     * @return
     */
    @Override
    public PresignedUploadResponse generateUploadPresignedUrl(String filename) {
        if (StrUtil.isBlank(filename)) {
            throw new SoException("文件名不能为空");
        }
        if (filename.contains(Constants.SLASH) || filename.contains("\\") || filename.contains("..")) {
            throw new SoException("文件名不合法");
        }
        FileStorage fileStorage = fileStorageService.getFileStorage();
        if (!(fileStorage instanceof QiniuKodoFileStorage)) {
            throw new SoException("当前仅支持七牛云存储平台直传");
        }
        return generateQiniuUploadAuth((QiniuKodoFileStorage) fileStorage, filename);
    }

    /**
     * 七牛云 Kodo 仅支持 GET 预签名，前端直传使用表单上传：token + key + 上传域名
     */
    private PresignedUploadResponse generateQiniuUploadAuth(QiniuKodoFileStorage fileStorage, String originalFilename) {
        String ext = FileNameUtil.extName(originalFilename);
        String filename = IdGeneratorUtil.nextIdStr() + (StrUtil.isBlank(ext) ? "" : "." + ext);
        FileInfo fileInfo = new FileInfo(fileStorage.getBasePath(), buildDefaultPath(), filename);
        fileInfo.setOriginalFilename(originalFilename);
        fileInfo.setUrl("");
        fileInfo.setUploadStatus(Constant.FileInfoUploadStatus.INITIATE);
        fileInfo.setPlatform(fileStorage.getPlatform());
        String key = fileStorage.getFileKey(fileInfo);
        long expireSeconds = 10 * 60L;
        String token = fileStorage.getClient().getAuth().uploadToken(fileStorage.getBucketName(), key, expireSeconds, null);
        String uploadUrl;
        try {
            uploadUrl = fileStorage.getClient().getConfiguration().upHost(token);
        } catch (Exception e) {
            log.error("获取七牛云上传域名失败", e);
            throw new SoException("获取上传地址失败");
        }
        SpringUtil.getBean(FileDetailServiceImpl.class).save(fileInfo);
        PresignedUploadResponse response = new PresignedUploadResponse();
        response.setKey(key);
        response.setToken(token);
        response.setUrl(uploadUrl);
        response.setFilename(filename);
        response.setOriginalFilename(originalFilename);
        response.setFileId(fileInfo.getId());
        return response;
    }

}
