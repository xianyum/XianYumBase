package cn.xianyum.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.xianyum.system.entity.po.FileDetailEntity;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.hash.HashInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.system.service.FileDetailService;
import cn.xianyum.system.dao.FileDetailMapper;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;


/**
 * 文件记录表(FileDetail)service层实现
 *
 * @author zhangwei
 * @since 2026-01-19 22:20:15
 */
@Service
@Slf4j
public class FileDetailServiceImpl implements FileDetailService, FileRecorder {

	@Autowired
	private FileDetailMapper fileDetailMapper;


    @Override
    public boolean save(FileInfo fileInfo) {
        FileDetailEntity detail = toFileDetail(fileInfo);
        String id = IdUtil.getSnowflakeNextIdStr();
        detail.setId(id);
        int insert = this.fileDetailMapper.insert(detail);
        if(insert > 0){
            fileInfo.setId(id);
        }
        return insert > 0;
    }

    /**
     * 更新文件记录，可以根据文件 ID 或 URL 来更新文件记录，
     * 主要用在手动分片上传文件-完成上传，作用是更新文件信息
     * @param fileInfo
     */
    @Override
    public void update(FileInfo fileInfo) {
        FileDetailEntity detail = toFileDetail(fileInfo);
        LambdaQueryWrapper<FileDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(detail.getUrl()),FileDetailEntity::getUrl, detail.getUrl())
                .eq(StrUtil.isNotBlank(detail.getId()),FileDetailEntity::getId, detail.getId());
        this.fileDetailMapper.update(detail, queryWrapper);
    }

    /**
     * 根据 url 查询文件信息
     */
    @Override
    public FileInfo getByUrl(String url) {
        LambdaQueryWrapper<FileDetailEntity> queryWrapper = Wrappers.<FileDetailEntity>lambdaQuery().eq(FileDetailEntity::getUrl, url);
        return toFileInfo(this.fileDetailMapper.selectOne(queryWrapper));

    }

    /**
     * 根据 url 删除文件信息
     */
    @Override
    public boolean delete(String url) {
        LambdaQueryWrapper<FileDetailEntity> queryWrapper = Wrappers.<FileDetailEntity>lambdaQuery().eq(FileDetailEntity::getUrl, url);
        return this.fileDetailMapper.delete(queryWrapper) > 0;
    }

    /**
     * 保存文件分片信息
     * todo 暂时不涉及所以不需要
     * @param filePartInfo 文件分片信息
     */
    @Override
    public void saveFilePart(FilePartInfo filePartInfo) {
        log.error("尚未实现保存分片信息：{}",JSONObject.toJSONString(filePartInfo));
    }


    /**
     * 删除文件分片信息
     */
    @Override
    public void deleteFilePartByUploadId(String uploadId) {
        log.error("尚未实现删除分片信息：{}",uploadId);
    }


    /**
     * 将 FileInfo 转为 FileDetail
     */
    public FileDetailEntity toFileDetail(FileInfo info){
        FileDetailEntity detail = BeanUtil.copyProperties(
                info, FileDetailEntity.class, "metadata", "userMetadata", "thMetadata", "thUserMetadata", "attr", "hashInfo");

        // 这里手动获 元数据 并转成 json 字符串，方便存储在数据库中
        detail.setMetadata(valueToJson(info.getMetadata()));
        detail.setUserMetadata(valueToJson(info.getUserMetadata()));
        detail.setThMetadata(valueToJson(info.getThMetadata()));
        detail.setThUserMetadata(valueToJson(info.getThUserMetadata()));
        // 这里手动获 取附加属性字典 并转成 json 字符串，方便存储在数据库中
        detail.setAttr(valueToJson(info.getAttr()));
        // 这里手动获 哈希信息 并转成 json 字符串，方便存储在数据库中
        detail.setHashInfo(valueToJson(info.getHashInfo()));
        return detail;
    }

    /**
     * 将 FileDetail 转为 FileInfo
     */
    public FileInfo toFileInfo(FileDetailEntity detail){
        FileInfo info = BeanUtil.copyProperties(
                detail, FileInfo.class, "metadata", "userMetadata", "thMetadata", "thUserMetadata", "attr", "hashInfo");

        // 这里手动获取数据库中的 json 字符串 并转成 元数据，方便使用
        info.setMetadata(jsonToMetadata(detail.getMetadata()));
        info.setUserMetadata(jsonToMetadata(detail.getUserMetadata()));
        info.setThMetadata(jsonToMetadata(detail.getThMetadata()));
        info.setThUserMetadata(jsonToMetadata(detail.getThUserMetadata()));
        // 这里手动获取数据库中的 json 字符串 并转成 附加属性字典，方便使用
        info.setAttr(jsonToDict(detail.getAttr()));
        // 这里手动获取数据库中的 json 字符串 并转成 哈希信息，方便使用
        info.setHashInfo(jsonToHashInfo(detail.getHashInfo()));
        return info;
    }

    /**
     * 将指定值转换成 json 字符串
     */
    public String valueToJson(Object value){
        if (value == null){
            return null;
        }
        return JSONObject.toJSONString(value);
    }

    /**
     * 将 json 字符串转换成元数据对象
     */
    public Map<String, String> jsonToMetadata(String json){
        if (StrUtil.isBlank(json)){
            return null;
        }
        return JSONObject.parseObject(json, new TypeReference<Map<String, String>>() {});
    }

    /**
     * 将 json 字符串转换成字典对象
     */
    public Dict jsonToDict(String json){
        if (StrUtil.isBlank(json)){
            return null;
        }
        return JSONObject.parseObject(json, Dict.class);
    }

    /**
     * 将 json 字符串转换成哈希信息对象
     */
    public HashInfo jsonToHashInfo(String json){
        if (StrUtil.isBlank(json)){
            return null;
        }
        return JSONObject.parseObject(json, HashInfo.class);
    }

    /**
     * 通过ID查询文件
     *
     * @param fileId
     * @return
     */
    @Override
    public FileInfo selectFileById(String fileId) {
        FileDetailEntity fileDetailEntity = this.fileDetailMapper.selectById(fileId);
        if(Objects.isNull(fileDetailEntity)){
            return null;
        }
        return toFileInfo(fileDetailEntity);
    }
}

