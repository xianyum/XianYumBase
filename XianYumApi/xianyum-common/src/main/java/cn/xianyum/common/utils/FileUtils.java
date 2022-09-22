package cn.xianyum.common.utils;


import cn.xianyum.common.config.XianYumConfig;
import cn.xianyum.common.exception.SoException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.URL;
import java.util.Date;

/**
 * 文件工具类
 * @author zhangwei
 * @date 2020/12/23 22:09
 */
@Slf4j
public class FileUtils {

    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 默认上传的地址
     */
    private static String defaultBaseDir = XianYumConfig.getXianYumConfig().getProfile();


    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String upload(MultipartFile file) throws IOException {
        try
        {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file 上传的文件
     * @return 文件名称
     * @throws IOException
     */
    public static final String upload(String baseDir, MultipartFile file) throws IOException {
        try
        {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir 相对应用的基目录
     * @param file 上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws IOException 比如读写文件出错时
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension) throws IOException{
        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > FileUtils.DEFAULT_FILE_NAME_LENGTH)
        {
            throw new SoException("上传的文件大小超出限制的文件大小！");
        }

        assertAllowed(file, allowedExtension);
        String fileName = extractFilename(file);
        File desc = getAbsoluteFile(baseDir, fileName);
        FileOutputStream downloadFile = new FileOutputStream(desc);
        downloadFile.write(file.getBytes());
        downloadFile.flush();
        downloadFile.close();
        String pathFileName = getPathFileName(baseDir, fileName);
        return pathFileName;
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        fileName = DateUtils.format(new Date(),"yyyy/MM/dd") + "/" + UUIDUtils.UUIDReplace() + "." + extension;
        return fileName;
    }

    private static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException
    {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.getParentFile().exists())
        {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists())
        {
            desc.createNewFile();
        }
        return desc;
    }

    private static final String getPathFileName(String uploadDir, String fileName) throws IOException {
        int dirLastIndex = XianYumConfig.getXianYumConfig().getProfile().length() + 1;
        String currentDir = StringUtil.substring(uploadDir, dirLastIndex);
        String pathFileName = "/" + currentDir + "/" + fileName;
        return pathFileName;
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @return
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension) {
        long size = file.getSize();
        if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE)
        {
            throw new SoException("上传的文件大小超出限制的文件大小！");
        }

        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension))
        {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION)
            {
                throw new SoException("不能上传此类型！");
            }
            else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION)
            {
                throw new SoException("不能上传此类型！");
            }
            else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION)
            {
                throw new SoException("不能上传此类型！");
            }
            else
            {
                throw new SoException("不能上传此类型！");
            }
        }

    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension)
        {
            if (str.equalsIgnoreCase(extension))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtil.isEmpty(extension))
        {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    /**
     * 获取文件流
     *
     * @param path 图像路径
     * @return 文件流
     */
    public static byte[] getAvatarImage(String path){
        String imagePath = getDefaultBaseDir()+path;
        File file = new File(imagePath);
        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte[] b = new byte[1024];
            int len = 0 ;
            while ((len = bis.read(b)) != -1){
                bos.write(b,0,len);
            }
            return bos.toByteArray();
        } catch (Exception e){
            log.error("读取文件出错,{},{}",imagePath,e.getMessage());
        }finally {
            try {
                bis.close();
                bos.close();
            }catch (Exception e){
                log.error("关闭流异常,{},{}",imagePath,e.getMessage());
            }
        }
        return null;
    }

    /**
     *
     * @param webUrl 网络资源地址
     * @param filePath 文件名称，比如 【1.jpg】
     */
    public static File copyByUrl(String webUrl,String filePath) throws Exception {

        System.out.println();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(webUrl);
            is = url.openStream();
            File file = new File(filePath);
            // 不存在时在从网络下载
            if (!file.exists()) {

                File parent = file.getParentFile();
                if (parent == null) {
                    throw new SoException("不正确的下载路径：" + file.getPath());
                }

                if (!parent.exists() && !parent.mkdirs()) {
                    throw new SoException("不能创建父目录：" + parent.getPath());
                }

                if (!file.createNewFile()) {
                    throw new SoException("文件刚被其它线程占用：" + parent.getPath());
                }
                fos = new FileOutputStream(file);
                byte[] buffer = new byte[2048];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            }
            return file;
        }catch (Exception e){
            log.error("网络资源下载文件异常，{}",e);
            throw new SoException("网络资源下载文件异常");
        }finally {
            if(fos!=null){
                fos.close();
            }
            if(is!=null){
                is.close();
            }
        }
    }
}
