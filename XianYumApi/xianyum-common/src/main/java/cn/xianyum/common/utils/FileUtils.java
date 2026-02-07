package cn.xianyum.common.utils;

import cn.xianyum.common.entity.file.FileDetailResponse;
import cn.xianyum.common.exception.SoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Method;

/**
 * 文件工具类
 * @author zhangwei
 * @date 2020/12/23 22:09
 */
@Slf4j
public class FileUtils {

    /** FileService注册bean名称 */
    private static final String FILE_SERVICE_BEAN_NAME = "fileServiceImpl";
    /** uploadFile方法名 */
    private static final String UPLOAD_FILE_METHOD = "uploadFile";
    /** selectFileById方法名 */
    private static final String SELECT_FILE_BY_ID_METHOD = "selectFileById";

    // 缓存Method对象，提升反射性能
    private static final Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
    /**
     * 反射调用FileService.uploadFile方法
     * @param file 上传的文件
     * @return common包的FileDetailResponse（强类型）
     * @throws Exception 反射调用异常（可封装为自定义异常）
     */
    public static FileDetailResponse uploadFile(MultipartFile file){
        try {
            // 1. 校验入参
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("上传文件不能为空");
            }

            // 2. 获取Spring容器中的FileService实例
            Object fileServiceBean = SpringUtils.getBean(FILE_SERVICE_BEAN_NAME);
            if (fileServiceBean == null) {
                throw new IllegalStateException("未找到FileService实例（请确认system模块已加载）");
            }

            // 3. 获取uploadFile方法（缓存优化）
            Method uploadMethod = getCachedMethod(
                    fileServiceBean.getClass(),
                    UPLOAD_FILE_METHOD,
                    MultipartFile.class
            );

            // 4. 反射调用并返回强类型（FileDetailResponse在common包，可直接强转）
            Object result = uploadMethod.invoke(fileServiceBean, file);
            return result == null ? null : (FileDetailResponse) result;
        }catch (Exception e){
            log.error("上传文件异常.",e);
            throw new SoException("上传文件异常");
        }
    }


    /**
     * 查询文件下载链接
     * @param fileId
     * @param isCached true 带缓存 false 不带缓存
     * @return
     */
    public static FileDetailResponse selectFileById(String fileId,boolean isCached){
        try {
            // 1. 校验入参
            if (fileId == null || fileId.trim().isEmpty()) {
                throw new IllegalArgumentException("文件ID不能为空");
            }

            // 2. 获取FileService实例
            Object fileServiceBean = SpringUtils.getBean(FILE_SERVICE_BEAN_NAME);
            if (fileServiceBean == null) {
                throw new IllegalStateException("未找到FileService实例（请确认system模块已加载）");
            }

            // 3. 获取selectFileById方法（缓存优化）
            Method selectMethod = getCachedMethod(
                    fileServiceBean.getClass(),
                    SELECT_FILE_BY_ID_METHOD,
                    String.class,
                    boolean.class
            );

            // 4. 反射调用并返回强类型
            Object result = selectMethod.invoke(fileServiceBean,fileId,isCached);
            return result == null ? null : (FileDetailResponse) result;
        }catch (Exception e){
            log.error("获取文件信息异常.",e);
            throw new SoException("获取文件信息异常");
        }
    }

    /**
     * 反射调用FileService.selectFileById方法
     * @param fileId 文件ID
     * @return common包的FileDetailResponse（强类型）
     * @throws Exception 反射调用异常
     */
    public static FileDetailResponse selectFileById(String fileId){
        return selectFileById(fileId,true);
    }

    /**
     * 缓存反射Method对象，避免重复获取
     */
    private static Method getCachedMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws Exception {
        // 构建缓存key：类名+方法名+参数类型
        String cacheKey = clazz.getName() + "#" + methodName + buildParamKey(parameterTypes);
        if (METHOD_CACHE.containsKey(cacheKey)) {
            return METHOD_CACHE.get(cacheKey);
        }

        // 优先从实现类找方法，找不到则从接口（FileService）找
        Method method = null;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // 从接口查找（兼容接口定义、实现类未重写的场景）
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> inter : interfaces) {
                if (FILE_SERVICE_BEAN_NAME.equals(inter.getName())) {
                    method = inter.getMethod(methodName, parameterTypes);
                    break;
                }
            }
            if (method == null) {
                throw new NoSuchMethodException("未找到方法：" + methodName + "，参数类型：" + buildParamKey(parameterTypes));
            }
        }

        // 设置可访问（避免私有/保护方法调用失败）
        method.setAccessible(true);
        METHOD_CACHE.put(cacheKey, method);
        return method;
    }

    /**
     * 构建参数类型的缓存key
     */
    private static String buildParamKey(Class<?>... parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Class<?> paramType : parameterTypes) {
            sb.append(paramType.getName()).append(",");
        }
        return sb.toString();
    }

}
