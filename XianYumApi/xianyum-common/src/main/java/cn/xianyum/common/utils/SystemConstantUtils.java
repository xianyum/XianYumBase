package cn.xianyum.common.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author
 * @description 提供获取系统常用变量工具类
 * @date 2022/4/21 10:46
 */
@Slf4j
public class SystemConstantUtils {

    public static String getValueByKey(String key) {
        try {
            Class<?> clazz = Class.forName("cn.xianyum.system.service.SystemConstantService");
            Method m = clazz.getDeclaredMethod("getValueKey",String.class);
            Object object = m.invoke(SpringUtils.getBean(clazz), key);
            return object == null ? null : String.valueOf(object);
        } catch (Exception var5) {
            log.error("通过反射获取系统常用变量异常. ",var5);
            return null;
        }
    }

    /**
     * 通过常量key获取JsonObject
     * @param key
     * @return
     */
    public static JSONObject getValueObjectByKey(String key) {
        String value = getValueByKey(key);
        return JSONObject.parseObject(value);
    }
}
