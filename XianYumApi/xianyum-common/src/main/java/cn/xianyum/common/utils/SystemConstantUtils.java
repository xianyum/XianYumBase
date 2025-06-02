package cn.xianyum.common.utils;

import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.exception.SoException;
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

    /**
     * 通过常量key获取值
     * @param systemConstantKeyEnum
     * @return
     */
    private static String getValueByKey(SystemConstantKeyEnum systemConstantKeyEnum) {
        try {
            Class<?> clazz = Class.forName("cn.xianyum.system.service.SystemConstantService");
            Method m = clazz.getDeclaredMethod("getValueKey",String.class);
            Object object = m.invoke(SpringUtils.getBean(clazz), systemConstantKeyEnum.getKey());
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
            Class<?> clazz = Class.forName("cn.xianyum.system.service.SystemConstantService");
            Method m = clazz.getDeclaredMethod("saveOrUpdate",SystemConstantKeyEnum.class,String.class,Integer.class);
            Object object = m.invoke(SpringUtils.getBean(clazz), systemConstantKeyEnum,value,visible);
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
        if(StringUtil.isBlank(value)){
            throw new SoException("系统常量不存在："+systemConstantKeyEnum.getKey());
        }
        return JSONObject.parseObject(value);
    }
}
