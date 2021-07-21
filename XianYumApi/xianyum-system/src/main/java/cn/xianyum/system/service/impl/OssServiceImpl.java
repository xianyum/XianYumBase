package cn.xianyum.system.service.impl;


import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.FileUtils;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.system.entity.po.OssFileEntity;
import cn.xianyum.system.service.OssService;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author zhangwei
 * @date 2019/11/30 14:55
 * @desc
 */
@Service
@Slf4j
public class OssServiceImpl implements OssService {

    @Value("${oss.access_key}")
    private String ACCESS_KEY;

    @Value("${oss.secret_key}")
    private String SECRET_KEY;

    @Value("${oss.bucket}")
    private String BUCKET;

    @Value("${oss.url}")
    private String URL;


    @Override
    public OssFileEntity upload(MultipartFile file) {
        if(file.isEmpty()){
            throw new SoException("文件不能为空！");
        }
        try {
            OssFileEntity ossFileEntity = new OssFileEntity();
            //获取到文件后缀名
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            Configuration cfg = new Configuration(Region.region2());
            UploadManager uploadManager = new UploadManager(cfg);
            Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
            String upToken = auth.uploadToken(BUCKET);
            String fileName = UUIDUtils.UUIDReplace() +suffix;
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            if(response.statusCode == 200){ //上传成功
                ossFileEntity.setFileName(file.getOriginalFilename());
                ossFileEntity.setUrl(URL+fileName);
                Long size = file.getSize()/1024;//转换成kb
                ossFileEntity.setSize(size);
            }else{
                ossFileEntity=null;
            }
            return ossFileEntity;
        }catch (Exception e){
            log.error("上传文件失败,{}",e.getMessage());
        }
        return null;
    }

    @Override
    public String getWebUpToken() {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(BUCKET);
        return upToken;
    }

    @Override
    public byte[] getImage(String path) {
        byte[] avatarImage = FileUtils.getAvatarImage(path);
        return avatarImage;
    }
}
