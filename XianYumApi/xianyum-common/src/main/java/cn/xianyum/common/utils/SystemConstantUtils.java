package cn.xianyum.common.utils;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.exception.SoException;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 * @description 提供获取系统常用变量工具类
 * @date 2022/4/21 10:46
 */
@Slf4j
public class SystemConstantUtils {

    /**
     * SystemConstantService 全限定类名，用于反射加载（跨模块无法直接依赖）
     */
    private static final String SYSTEM_CONSTANT_SERVICE = "cn.xianyum.system.service.SystemConstantService";

    /**
     * 缓存 SystemConstantService Bean，避免每次调用都通过反射获取
     */
    private static volatile Object systemConstantService;


    /**
     * 双重检查锁懒加载获取 SystemConstantService Bean
     */
    private static Object getSystemConstantService() {
        if (systemConstantService == null) {
            synchronized (SystemConstantUtils.class) {
                if (systemConstantService == null) {
                    try {
                        Class<?> clazz = Class.forName(SYSTEM_CONSTANT_SERVICE);
                        systemConstantService = SpringUtil.getBean(clazz);
                    } catch (Exception e) {
                        log.error("获取SystemConstantService Bean异常. ", e);
                    }
                }
            }
        }
        return systemConstantService;
    }

    /**
     * 通过常量key获取值
     * @param systemConstantKeyEnum
     * @return
     */
    public static String getValueByKey(SystemConstantKeyEnum systemConstantKeyEnum) {
        try {
            Object object = ReflectUtil.invoke(getSystemConstantService(), "getValueKey", systemConstantKeyEnum.getKey());
            return object == null ? null : String.valueOf(object);
        } catch (Exception var5) {
            log.error("通过反射获取系统常用变量异常. ",var5);
            return null;
        }
    }

    /**
     *
     * @param systemConstantKeyEnum 常量keyEnum
     * @param value 常量value
     * @param visible 是否可见， 0:公用 1：私有
     * @return
     */
    public static boolean saveSystemConstant(SystemConstantKeyEnum systemConstantKeyEnum,String value,Integer visible) {
        try {
            Object object = ReflectUtil.invoke(getSystemConstantService(), "saveOrUpdate", systemConstantKeyEnum, value, visible);
            return (Boolean) object;
        } catch (Exception var5) {
            log.error("通过反射插入系统常用变量异常. ",var5);
            return false;
        }
    }

    /**
     * 通过常量key获取JsonObject
     * @param systemConstantKeyEnum
     * @return
     */
    public static JSONObject getValueObjectByKey(SystemConstantKeyEnum systemConstantKeyEnum) {
        String value = getValueByKey(systemConstantKeyEnum);
        if(StrUtil.isBlank(value)){
            throw new SoException("系统常量不存在："+systemConstantKeyEnum.getKey());
        }
        return JSONObject.parseObject(value);
    }
}
